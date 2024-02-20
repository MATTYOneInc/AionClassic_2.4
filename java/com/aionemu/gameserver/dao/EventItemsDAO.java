package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wanke on 03/03/2017.
 */
public class EventItemsDAO
{
	private static final Logger log = LoggerFactory.getLogger(EventItemsDAO.class);

	public static final String INSERT_QUERY = "INSERT INTO `event_items` (`player_id`, `item_id`, `counts`) VALUES (?,?,?)";
	public static final String DELETE_QUERY = "DELETE FROM `event_items` WHERE `player_id`=?";
	public static final String SELECT_QUERY = "SELECT `item_id`, `counts` FROM `event_items` WHERE `player_id`=?";

	public static void loadItems(final Player player)
	{
		DB.select(SELECT_QUERY, new ParamReadStH()
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
					int itemId = rset.getInt("item_id");
					int counts = rset.getInt("counts");
				}
			}
		});
	}

	public static void storeItems(Player player)
	{
	}

	public static void deleteItems(final int itemId)
	{
		DB.insertUpdate("DELETE FROM `event_items` WHERE `item_id`= ?", stmt -> {
			stmt.setInt(1, itemId);
			stmt.execute();
		});
	}

	private static void deleteItems(final Player player)
	{
		DB.insertUpdate(DELETE_QUERY, stmt -> {
			stmt.setInt(1, player.getObjectId());
			stmt.execute();
		});
	}
}
