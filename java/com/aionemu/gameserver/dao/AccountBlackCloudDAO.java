package com.aionemu.gameserver.dao;


import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.gameobjects.blackcloud.BlackcloudItem;
import com.aionemu.gameserver.model.gameobjects.blackcloud.BlackcloudLetter;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AccountBlackCloudDAO extends IDFactoryAwareDAO
{
	private static final Logger log = LoggerFactory.getLogger(AccountBlackCloudDAO.class);

	public static Map<Integer, BlackcloudLetter> loadAccountBlackcloud(Account account)
	{
		Map<Integer, BlackcloudLetter> letterMap = new FastMap<Integer, BlackcloudLetter>();
		DB.select("SELECT * FROM `account_blackcloud` WHERE `owner`=?", new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, account.getId());
			}

			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while (rset.next()) {
					BlackcloudLetter entry = new BlackcloudLetter(rset.getInt("id"), rset.getString("title"), rset.getString("message"), rset.getTimestamp("received_date"), true);
					entry.setAttachedItem(loadBlackcloudItem(entry));
					letterMap.put(rset.getInt("id"), entry);
				}
			}
		});

		return letterMap;
	}

	public static boolean addBlackcloud(Account account, BlackcloudLetter blackcloudLetter)
	{
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement("INSERT INTO account_blackcloud (id, owner, title, message, received_date) VALUES (?, ?, ?, ?, ?)");
			stmt.setInt(1, blackcloudLetter.getObjectId());
			stmt.setInt(2, account.getId());
			stmt.setString(3, blackcloudLetter.getTitle());
			stmt.setString(4, blackcloudLetter.getMessage());
			stmt.setTimestamp(5, blackcloudLetter.getTimeStamp());
			stmt.execute();
			stmt.close();
			return true;
		} catch (SQLException e) {
			log.error("Could not insert Blackcloud letter data for Account " + account.getName() + " from DB: " + e.getMessage(), e);
			return false;
		} finally {
			DatabaseFactory.close(con);
		}
	}

	public static boolean updateBlackcloud(int i)
	{
		return false;
	}

	public static void deleteBlackcloud(int id)
	{
		PreparedStatement statement = DB.prepareStatement("DELETE FROM account_blackcloud WHERE id = ?");
		try {
			statement.setInt(1, id);
		} catch (SQLException e) {
			log.error("Some crap, can't set int parameter to PreparedStatement", e);
		}

		DB.executeUpdateAndClose(statement);
	}

	public static List<BlackcloudItem> loadBlackcloudItem(BlackcloudLetter blackcloudLetter)
	{
		List<BlackcloudItem> blackcloudItems = new FastList<BlackcloudItem>();
		DB.select("SELECT * FROM `blackcloud_item` WHERE `blackcloud_id`=?", new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement stmt) throws SQLException
			{
				stmt.setInt(1, blackcloudLetter.getObjectId());
			}

			@Override
			public void handleRead(ResultSet rset) throws SQLException
			{
				while (rset.next()) {
					BlackcloudItem item = new BlackcloudItem(rset.getInt("blackcloud_id"), rset.getInt("item_id"), rset.getInt("item_count"));
					blackcloudItems.add(item);
				}
			}
		});

		return blackcloudItems;
	}

	public static boolean addBlackcloudItem(BlackcloudItem blackcloudItem)
	{
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement("INSERT INTO blackcloud_item (blackcloud_id, item_id, item_count) VALUES (?, ?, ?)");
			stmt.setInt(1, blackcloudItem.getBlackCloudId());
			stmt.setInt(2, blackcloudItem.getItemId());
			stmt.setInt(3, blackcloudItem.getItemCount());
			stmt.execute();
			stmt.close();
			return true;
		} catch (SQLException e) {
			log.error("Could not insert blackcloud item data from DB: " + e.getMessage(), e);
			return false;
		} finally {
			DatabaseFactory.close(con);
		}
	}

	public static void deleteBlackcloudItem(int id)
	{
		PreparedStatement statement = DB.prepareStatement("DELETE FROM blackcloud_item WHERE blackcloud_id = ?");
		try {
			statement.setInt(1, id);
		} catch (SQLException e) {
			log.error("Some crap, can't set int parameter to PreparedStatement", e);
		}

		DB.executeUpdateAndClose(statement);
	}

	public static int[] getUsedIDs()
	{
		PreparedStatement statement = DB.prepareStatement("SELECT id FROM account_blackcloud", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

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
			log.error("Can't get list of id's from inventory wardrobe table", e);
		} finally {
			DB.close(statement);
		}

		return new int[0];
	}
}
