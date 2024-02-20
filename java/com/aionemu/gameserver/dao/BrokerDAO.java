package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.DB;
import com.aionemu.gameserver.model.broker.BrokerRace;
import com.aionemu.gameserver.model.gameobjects.BrokerItem;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BrokerDAO extends IDFactoryAwareDAO
{
	private static final Logger log = LoggerFactory.getLogger(BrokerDAO.class);

	public static List<BrokerItem> loadBroker()
	{
		final List<BrokerItem> brokerItems = new ArrayList<BrokerItem>();

		final List<Item> items = getBrokerItems();

		if (items.size() > 0) {
			ItemStoneListDAO.load(items);
		}

		DB.select("SELECT * FROM broker", rset -> {
			while (rset.next()) {
				int itemPointer = rset.getInt("item_pointer");
				int itemId = rset.getInt("item_id");
				long itemCount = rset.getLong("item_count");
				String itemCreator = rset.getString("item_creator");
				String seller = rset.getString("seller");
				int sellerId = rset.getInt("seller_id");
				long price = rset.getLong("price");
				BrokerRace itemBrokerRace = BrokerRace.valueOf(rset.getString("broker_race"));
				Timestamp expireTime = rset.getTimestamp("expire_time");
				Timestamp settleTime = rset.getTimestamp("settle_time");
				int sold = rset.getInt("is_sold");
				int settled = rset.getInt("is_settled");
				int SplitSell = rset.getInt("is_splitsell");
				boolean isSold = sold == 1;
				boolean isSettled = settled == 1;
				boolean isSplitSell = SplitSell == 1;

				Item item = null;
				if (!isSold)
					for (Item brItem : items) {
						if (itemPointer == brItem.getObjectId()) {
							item = brItem;
							break;
						}
					}

				brokerItems.add(new BrokerItem(item, itemId, itemPointer, itemCount, itemCreator, price, seller, sellerId, itemBrokerRace, isSold, isSettled, expireTime, settleTime, isSplitSell));
			}
		});

		return brokerItems;
	}

	public static boolean store(BrokerItem item)
	{
		boolean result = false;

		if (item == null) {
			log.warn("Null broker item on save");
			return result;
		}

		switch (item.getPersistentState()) {
			case NEW:
				result = insertBrokerItem(item);
				if (item.getItem() != null)
					InventoryDAO.store(item.getItem(), item.getSellerId());
				break;

			case DELETED:
				result = deleteBrokerItem(item);
				break;

			case UPDATE_ITEM_BROKER:
				result = updateItem(item);
				break;

			case UPDATE_REQUIRED:
				result = updateBrokerItem(item);
				break;
		}

		if (result)
			item.setPersistentState(PersistentState.UPDATED);

		return result;
	}

	public static boolean preBuyCheck(int itemForCheck)
	{
		PreparedStatement st = DB.prepareStatement("SELECT * FROM broker WHERE `item_pointer` = ? and `is_sold` = 0");
		log.info("Checking broker item: " + itemForCheck);
		try {
			st.setInt(1, itemForCheck);

			ResultSet rs = st.executeQuery();

			if (rs.next())
				return true;
		} catch (SQLException e) {
			log.error("Can't to prebuy broker check: ", e);
		} finally {
			DB.close(st);
		}

		return false;
	}

	private static List<Item> getBrokerItems()
	{
		final List<Item> brokerItems = new ArrayList<Item>();

		DB.select("SELECT * FROM inventory WHERE `item_location` = 126", rset -> {
			while (rset.next()) {
				int itemUniqueId = rset.getInt("item_unique_id");
				int itemId = rset.getInt("item_id");
				long itemCount = rset.getLong("item_count");
				int itemColor = rset.getInt("item_color");
				int colorExpireTime = rset.getInt("color_expires");
				String itemCreator = rset.getString("item_creator");
				int expireTime = rset.getInt("expire_time");
				int activationCount = rset.getInt("activation_count");
				int slot = rset.getInt("slot");
				int location = rset.getInt("item_location");
				int enchant = rset.getInt("enchant");
				int itemSkin = rset.getInt("item_skin");
				int fusionedItem = rset.getInt("fusioned_item");
				int optionalSocket = rset.getInt("optional_socket");
				int optionalFusionSocket = rset.getInt("optional_fusion_socket");
				int charge = rset.getInt("charge");
				brokerItems.add(new Item(
						itemUniqueId,
						itemId,
						itemCount,
						itemColor,
						colorExpireTime,
						itemCreator,
						expireTime,
						activationCount,
						false,
						false,
						slot,
						location,
						enchant,
						itemSkin,
						fusionedItem,
						optionalSocket,
						optionalFusionSocket,
						charge)
				);
			}
		});

		return brokerItems;
	}

	private static boolean insertBrokerItem(final BrokerItem item)
	{
		return DB.insertUpdate("INSERT INTO `broker` (`item_pointer`, `item_id`, `item_count`, `item_creator`, `seller`, `price`, `broker_race`, `expire_time`, `settle_time`, `seller_id`, `is_sold`, `is_settled`,  `is_splitsell`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)", stmt -> {
			stmt.setInt(1, item.getItemUniqueId());
			stmt.setInt(2, item.getItemId());
			stmt.setLong(3, item.getItemCount());
			stmt.setString(4, item.getItemCreator());
			stmt.setString(5, item.getSeller());
			stmt.setLong(6, item.getPrice());
			stmt.setString(7, String.valueOf(item.getItemBrokerRace()));
			stmt.setTimestamp(8, item.getExpireTime());
			stmt.setTimestamp(9, item.getSettleTime());
			stmt.setInt(10, item.getSellerId());
			stmt.setBoolean(11, item.isSold());
			stmt.setBoolean(12, item.isSettled());
			stmt.setBoolean(13, item.isSplitSell());
			stmt.execute();
		});
	}

	private static boolean deleteBrokerItem(final BrokerItem item)
	{
		return DB.insertUpdate("DELETE FROM `broker` WHERE `item_pointer` = ? AND `seller_id` = ? AND `expire_time` = ?", stmt -> {
			stmt.setInt(1, item.getItemUniqueId());
			stmt.setInt(2, item.getSellerId());
			stmt.setTimestamp(3, item.getExpireTime());
			stmt.execute();
		});
	}

	private static boolean updateBrokerItem(final BrokerItem item)
	{
		return DB.insertUpdate("UPDATE broker SET `is_sold` = ?, `is_settled` = 1, `settle_time` = ? WHERE `item_pointer` = ? AND `expire_time` = ? AND `seller_id` = ?", stmt -> {
			stmt.setBoolean(1, item.isSold());
			stmt.setTimestamp(2, item.getSettleTime());
			stmt.setInt(3, item.getItemUniqueId());
			stmt.setTimestamp(4, item.getExpireTime());
			stmt.setInt(5, item.getSellerId());

			stmt.execute();
		});
	}

	private static boolean updateItem(final BrokerItem item)
	{
		return DB.insertUpdate("UPDATE broker SET `item_count` = ?, `price` = ?, `is_sold` = ?, `is_settled` = ?, `settle_time` = ?, `is_splitsell` = ? WHERE `item_pointer` = ? AND `expire_time` = ? AND `seller_id` = ? AND `is_settled` = 0", stmt -> {
			stmt.setLong(1, item.getItemCount());
			stmt.setLong(2, item.getPrice());
			stmt.setBoolean(3, item.isSold());
			stmt.setBoolean(4, item.isSettled());
			stmt.setTimestamp(5, item.getSettleTime());
			stmt.setBoolean(6, item.isSplitSell());
			stmt.setInt(7, item.getItemUniqueId());
			stmt.setTimestamp(8, item.getExpireTime());
			stmt.setInt(9, item.getSellerId());

			stmt.execute();
		});
	}

	public static int[] getUsedIDs()
	{
		PreparedStatement statement = DB.prepareStatement("SELECT id FROM players", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		try {
			ResultSet rs = statement.executeQuery();
			rs.last();
			int count = rs.getRow();
			rs.beforeFirst();
			int[] ids = new int[count];
			for (int i = 0; i < count; i++) {
				rs.next();
				ids[i] = rs.getInt("id");
			}
			return ids;
		} catch (SQLException e) {
			log.error("Can't get list of id's from players table", e);
		} finally {
			DB.close(statement);
		}

		return new int[0];
	}
}
