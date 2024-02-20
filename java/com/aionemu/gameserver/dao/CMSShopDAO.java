package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.model.templates.rewards.RewardEntryItem;
import javolution.util.FastList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author KID
 */
public class CMSShopDAO
{
	private static final Logger log = LoggerFactory.getLogger(CMSShopDAO.class);
	public static final String UPDATE_QUERY = "DELETE FROM  `cms_reward` WHERE `unique` = ?";
	public static final String SELECT_QUERY = "SELECT * FROM `cms_reward` WHERE `item_owner`=? AND `rewarded`=?";

	public static FastList<RewardEntryItem> getAvailable(int playerId)
	{
		FastList<RewardEntryItem> list = FastList.newInstance();

		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, playerId);
			stmt.setInt(2, 0);

			ResultSet rset = stmt.executeQuery();
			while (rset.next()) {
				int unique = rset.getInt("unique");
				int item_id = rset.getInt("item_id");
				int count = rset.getInt("item_count");
				list.add(new RewardEntryItem(unique, item_id, count));
			}
			rset.close();
			stmt.close();
		} catch (Exception e) {
			log.warn("getAvailable() for " + playerId + " from DB: " + e.getMessage(), e);
		} finally {
			DatabaseFactory.close(con);
		}

		return list;
	}

	public static void uncheckAvailable(FastList<Integer> ids)
	{
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt;
			for (int uniqid : ids) {
				stmt = con.prepareStatement(UPDATE_QUERY);
				stmt.setInt(1, uniqid);
				stmt.execute();
				stmt.close();
			}
		} catch (Exception e) {
			log.error("uncheckAvailable", e);
		} finally {
			DatabaseFactory.close(con);
		}
	}
}
