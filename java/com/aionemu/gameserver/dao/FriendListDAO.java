package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.model.gameobjects.player.Friend;
import com.aionemu.gameserver.model.gameobjects.player.FriendList;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FriendListDAO
{
	private static final Logger log = LoggerFactory.getLogger(FriendListDAO.class);
	public static final String LOAD_QUERY = "SELECT * FROM `friends` WHERE `player`=?";
	public static final String ADD_QUERY = "INSERT INTO `friends` (`player`,`friend`) VALUES (?, ?)";
	public static final String DEL_QUERY = "DELETE FROM friends WHERE player = ? AND friend = ?";
	public static final String SET_NOTE = "UPDATE `friends` SET `note` = ? WHERE `player` = ? AND `friend` = ?";

	public static FriendList load(final Player player)
	{
		final List<Friend> friends = new ArrayList<Friend>();
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(LOAD_QUERY);
			stmt.setInt(1, player.getObjectId());
			ResultSet rset = stmt.executeQuery();
			while (rset.next()) {
				int objId = rset.getInt("friend");

				PlayerCommonData pcd = PlayerDAO.loadPlayerCommonData(objId);
				if (pcd != null) {
					Friend friend = new Friend(pcd);
					friends.add(friend);
				}
			}
		} catch (Exception e) {
			log.error("Could not restore QuestStateList data for player: " + player.getObjectId() + " from DB: " + e.getMessage(), e);
		} finally {
			DatabaseFactory.close(con);
		}

		return new FriendList(player, friends);
	}

	public static boolean addFriends(final Player player, final Player friend)
	{
		return DB.insertUpdate(ADD_QUERY, ps -> {
			ps.setInt(1, player.getObjectId());
			ps.setInt(2, friend.getObjectId());
			ps.addBatch();

			ps.setInt(1, friend.getObjectId());
			ps.setInt(2, player.getObjectId());
			ps.addBatch();

			ps.executeBatch();
		});
	}

	public static boolean delFriends(final int playerOid, final int friendOid)
	{
		return DB.insertUpdate(DEL_QUERY, ps -> {
			ps.setInt(1, playerOid);
			ps.setInt(2, friendOid);
			ps.addBatch();

			ps.setInt(1, friendOid);
			ps.setInt(2, playerOid);
			ps.addBatch();

			ps.executeBatch();
		});
	}

	public static void setFriendNote(final int playerId, final int friendId, final String note)
	{
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(SET_NOTE);
			stmt.setString(1, note);
			stmt.setInt(2, playerId);
			stmt.setInt(3, friendId);
			stmt.execute();
			stmt.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			DatabaseFactory.close(con);
		}
	}
}
