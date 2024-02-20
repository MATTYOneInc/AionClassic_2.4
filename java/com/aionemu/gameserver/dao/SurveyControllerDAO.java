package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.model.templates.survey.SurveyItem;
import javolution.util.FastList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author KID
 */
public class SurveyControllerDAO
{
	private static final Logger log = LoggerFactory.getLogger(SurveyControllerDAO.class);
	public static final String UPDATE_QUERY = "UPDATE `surveys` SET `used`=?, used_time=NOW() WHERE `unique_id`=?";
	public static final String SELECT_QUERY = "SELECT * FROM `surveys` WHERE `used`=?";

	public static boolean useItem(int id)
	{
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt;
			stmt = con.prepareStatement(UPDATE_QUERY);
			stmt.setInt(1, 1);
			stmt.setInt(2, id);
			stmt.execute();
			stmt.close();
		} catch (Exception e) {
			log.error("useItem", e);
			return false;
		} finally {
			DatabaseFactory.close(con);
		}

		return true;
	}

	public static FastList<SurveyItem> getAllNew()
	{
		FastList<SurveyItem> list = FastList.newInstance();

		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, 0);

			ResultSet rset = stmt.executeQuery();
			while (rset.next()) {
				SurveyItem item = new SurveyItem();
				item.uniqueId = rset.getInt("unique_id");
				item.ownerId = rset.getInt("owner_id");
				item.itemId = rset.getInt("item_id");
				item.count = rset.getLong("item_count");
				item.html = rset.getString("html_text");
				item.radio = rset.getString("html_radio");
				list.add(item);
			}
			rset.close();
			stmt.close();
		} catch (Exception e) {
			log.warn("getAllNew() from DB: " + e.getMessage(), e);
		} finally {
			DatabaseFactory.close(con);
		}

		return list;
	}
}
