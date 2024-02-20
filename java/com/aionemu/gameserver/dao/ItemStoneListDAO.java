package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.utils.GenericValidator;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.GodStone;
import com.aionemu.gameserver.model.items.ItemStone;
import com.aionemu.gameserver.model.items.ManaStone;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ItemStoneListDAO
{
	private static final Logger log = LoggerFactory.getLogger(ItemStoneListDAO.class);
	public static final String INSERT_QUERY = "INSERT INTO `item_stones` (`item_unique_id`, `item_id`, `slot`, `category`) VALUES (?,?,?,?)";
	public static final String UPDATE_QUERY = "UPDATE `item_stones` SET `item_id`=?, `slot`=? where `item_unique_id`=? AND `category`=?";
	public static final String DELETE_QUERY = "DELETE FROM `item_stones` WHERE `item_unique_id`=? AND slot=? AND category=?";
	public static final String SELECT_QUERY = "SELECT * FROM `item_stones` WHERE `item_unique_id`=?";

	private static final Predicate<ItemStone> itemStoneAddPredicate = itemStone -> itemStone != null && PersistentState.NEW == itemStone.getPersistentState();
	private static final Predicate<ItemStone> itemStoneDeletedPredicate = itemStone -> itemStone != null && PersistentState.DELETED == itemStone.getPersistentState();
	private static final Predicate<ItemStone> itemStoneUpdatePredicate = itemStone -> itemStone != null && PersistentState.UPDATE_REQUIRED == itemStone.getPersistentState();

	public static void load(final Collection<Item> items)
	{
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			for (Item item : items) {
				if (item.getItemTemplate().isArmor() || item.getItemTemplate().isWeapon()) {
					stmt.setInt(1, item.getObjectId());
					ResultSet rset = stmt.executeQuery();
					while (rset.next()) {
						int itemId = rset.getInt("item_id");
						int slot = rset.getInt("slot");
						int stoneType = rset.getInt("category");
						switch (stoneType) {
							case 0:
								item.getItemStones().add(new ManaStone(item.getObjectId(), itemId, slot, PersistentState.UPDATED));
								break;
							case 1:
								item.setGodStone(new GodStone(item.getObjectId(), itemId, PersistentState.UPDATED));
								break;
							case 2:
								item.getFusionStones().add(new ManaStone(item.getObjectId(), itemId, slot, PersistentState.UPDATED));
								break;
						}
					}
					rset.close();
				}
			}
			stmt.close();
		} catch (Exception e) {
			//log.error("Could not restore ItemStoneList data from DB: " + e.getMessage(), e);
		} finally {
			DatabaseFactory.close(con);
		}
	}

	public static void storeManaStones(Set<ManaStone> manaStones)
	{
		store(manaStones, ItemStone.ItemStoneType.MANASTONE);
	}

	public static void storeFusionStones(Set<ManaStone> fusionStones)
	{
		store(fusionStones, ItemStone.ItemStoneType.FUSIONSTONE);
	}

	private static void store(Set<? extends ItemStone> stones, ItemStone.ItemStoneType ist)
	{
		if (GenericValidator.isBlankOrNull(stones)) {
			return;
		}
		Set<? extends ItemStone> stonesToAdd = Sets.filter(stones, itemStoneAddPredicate);
		Set<? extends ItemStone> stonesToDelete = Sets.filter(stones, itemStoneDeletedPredicate);
		Set<? extends ItemStone> stonesToUpdate = Sets.filter(stones, itemStoneUpdatePredicate);
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			con.setAutoCommit(false);
			deleteItemStones(con, stonesToDelete, ist);
			addItemStones(con, stonesToAdd, ist);
			updateItemStones(con, stonesToUpdate, ist);
		} catch (SQLException e) {
			//log.error("Can't save stones", e);
		} finally {
			DatabaseFactory.close(con);
		}
		for (ItemStone is : stones) {
			is.setPersistentState(PersistentState.UPDATED);
		}
	}

	public static void save(Player player)
	{
		save(player.getAllItems());
	}

	public static void save(List<Item> items)
	{
		if (GenericValidator.isBlankOrNull(items)) {
			return;
		}

		Set<ManaStone> manaStones = Sets.newHashSet();
		Set<ManaStone> fusionStones = Sets.newHashSet();
		Set<GodStone> godStones = Sets.newHashSet();
		for (Item item : items) {
			if (item.hasManaStones()) {
				manaStones.addAll(item.getItemStones());
			}
			if (item.hasFusionStones()) {
				fusionStones.addAll(item.getFusionStones());
			}
			GodStone godStone = item.getGodStone();
			if (godStone != null) {
				godStones.add(godStone);
			}
		}
		store(manaStones, ItemStone.ItemStoneType.MANASTONE);
		store(fusionStones, ItemStone.ItemStoneType.FUSIONSTONE);
		store(godStones, ItemStone.ItemStoneType.GODSTONE);
	}

	private static void addItemStones(Connection con, Collection<? extends ItemStone> itemStones, ItemStone.ItemStoneType ist)
	{
		if (GenericValidator.isBlankOrNull(itemStones)) {
			return;
		}
		PreparedStatement st = null;
		try {
			st = con.prepareStatement(INSERT_QUERY);
			for (ItemStone is : itemStones) {
				st.setInt(1, is.getItemObjId());
				st.setInt(2, is.getItemId());
				st.setInt(3, is.getSlot());
				st.setInt(4, ist.ordinal());
				st.addBatch();
			}
			st.executeBatch();
			con.commit();
		} catch (SQLException e) {
			//log.error("Error occured while saving item stones", e);
		} finally {
			DatabaseFactory.close(st);
		}
	}

	private static void updateItemStones(Connection con, Collection<? extends ItemStone> itemStones, ItemStone.ItemStoneType ist)
	{
		if (GenericValidator.isBlankOrNull(itemStones)) {
			return;
		}
		PreparedStatement st = null;
		try {
			st = con.prepareStatement(UPDATE_QUERY);
			for (ItemStone is : itemStones) {
				st.setInt(1, is.getItemId());
				st.setInt(2, is.getSlot());
				st.setInt(3, is.getItemObjId());
				st.setInt(4, ist.ordinal());
				st.addBatch();
			}
			st.executeBatch();
			con.commit();
		} catch (SQLException e) {
			//log.error("Error occured while saving item stones", e);
		} finally {
			DatabaseFactory.close(st);
		}
	}

	private static void deleteItemStones(Connection con, Collection<? extends ItemStone> itemStones, ItemStone.ItemStoneType ist)
	{
		if (GenericValidator.isBlankOrNull(itemStones)) {
			return;
		}
		PreparedStatement st = null;
		try {
			st = con.prepareStatement(DELETE_QUERY);
			for (ItemStone is : itemStones) {
				st.setInt(1, is.getItemObjId());
				st.setInt(2, is.getSlot());
				st.setInt(3, ist.ordinal());
				st.execute();
				st.addBatch();
			}
			st.executeBatch();
			con.commit();
		} catch (SQLException e) {
			//log.error("Error occured while saving item stones", e);
		} finally {
			DatabaseFactory.close(st);
		}
	}

	private static void deleteItemStone(Connection con, int uid, int slot, int category)
	{
		PreparedStatement st = null;
		try {
			st = con.prepareStatement(DELETE_QUERY);
			st.setInt(1, uid);
			st.setInt(2, slot);
			st.setInt(3, category);
			st.execute();
		} catch (SQLException e) {
			//log.error("Error occured while saving item stones", e);
		} finally {
			DatabaseFactory.close(st);
		}
	}
}
