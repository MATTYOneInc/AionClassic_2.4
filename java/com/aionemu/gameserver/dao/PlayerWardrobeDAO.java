package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.WardrobeEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerWardrobeDAO extends IDFactoryAwareDAO
{
	private static final Logger log = LoggerFactory.getLogger(PlayerWardrobeDAO.class);

	public static final String INSERT_QUERY = "INSERT INTO player_wardrobe (object_id, player_id, item_id) VALUES (?, ?, ?)";
	public static final String LOAD_QUERY = "SELECT * FROM `player_wardrobe` WHERE `player_id`=?";
	public static final String UPDATE_QUERY = "UPDATE player_wardrobe set `color`=?, `like`=? WHERE `object_id`=?";

	public static void loadWardrobe(Player player)
	{
		DB.select(LOAD_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, player.getObjectId());
			}

			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while (rset.next()) {
					WardrobeEntry entry = new WardrobeEntry(rset.getInt("object_id"), rset.getInt("item_id"), rset.getInt("color"), rset.getBoolean("like"));
					player.getPlayerWardrobe().put(rset.getInt("object_id"), entry);
				}
			}
		});
	}

	public static boolean addItem(Player player, WardrobeEntry wardrobeEntry)
	{
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
			stmt.setInt(1, wardrobeEntry.getObjectId());
			stmt.setInt(2, player.getObjectId());
			stmt.setInt(3, wardrobeEntry.getItemId());
			stmt.execute();
			stmt.close();
			return true;
		} catch (SQLException e) {
			log.error("Could not insert Inventory Wardrobe data for Player " + player.getName() + " from DB: " + e.getMessage(), e);
			return false;
		} finally {
			DatabaseFactory.close(con);
		}
	}

	public static boolean updateItem(Player player, WardrobeEntry wardrobeEntry)
	{
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
			stmt.setInt(1, wardrobeEntry.getColor());
			stmt.setBoolean(2, wardrobeEntry.isLiked());
			stmt.setInt(3, wardrobeEntry.getObjectId());
			stmt.execute();
			stmt.close();
		} catch (Exception e) {
			log.error("Could not update Inventory Wardrobe data for Player " + player.getName() + " from DB: " + e.getMessage(), e);
			return false;
		} finally {
			DatabaseFactory.close(con);
		}

		return true;
	}

	public static void deleteItem(Player player, WardrobeEntry wardrobeEntry)
	{
		PreparedStatement statement = DB.prepareStatement("DELETE FROM player_wardrobe WHERE object_id = ?");
		try {
			statement.setInt(1, wardrobeEntry.getObjectId());
		} catch (SQLException e) {
			log.error("Some crap, can't set int parameter to PreparedStatement", e);
		}

		DB.executeUpdateAndClose(statement);
	}

	public static int[] getUsedIDs()
	{
		PreparedStatement statement = DB.prepareStatement("SELECT object_id FROM player_wardrobe", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		try {
			ResultSet rs = statement.executeQuery();
			rs.last();
			int count = rs.getRow();
			rs.beforeFirst();
			int[] ids = new int[count];
			for (int i = 0; i < count; i++) {
				rs.next();
				ids[i] = rs.getInt("object_id");
			}
			return ids;
		} catch (SQLException e) {
			log.error("Can't get list of id's from inventory wardrobe table", e);
		} finally {
			DB.close(statement);
		}

		return new int[0];
	}
}
