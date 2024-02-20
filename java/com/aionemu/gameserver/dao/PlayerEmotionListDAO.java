/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.emotion.Emotion;
import com.aionemu.gameserver.model.gameobjects.player.emotion.EmotionList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Mr. Poke
 */
public class PlayerEmotionListDAO
{
	private static final Logger log = LoggerFactory.getLogger(PlayerEmotionListDAO.class);

	public static final String INSERT_QUERY = "INSERT INTO `player_emotions` (`player_id`, `emotion`, `remaining`) VALUES (?,?,?)";
	public static final String SELECT_QUERY = "SELECT `emotion`, `remaining` FROM `player_emotions` WHERE `player_id`=?";
	public static final String DELETE_QUERY = "DELETE FROM `player_emotions` WHERE `player_id`=? AND `emotion`=?";

	/**
	 * @param player Player
	 */
	public static void loadEmotions(Player player)
	{
		EmotionList emotions = new EmotionList(player);
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, player.getObjectId());
			ResultSet rset = stmt.executeQuery();
			while (rset.next()) {
				int emotionId = rset.getInt("emotion");
				int remaining = rset.getInt("remaining");
				emotions.add(emotionId, remaining, false);
			}
			rset.close();
			stmt.close();
		} catch (Exception e) {
			log.error("Could not restore emotionId for playerObjId: " + player.getObjectId() + " from DB: " + e.getMessage(), e);
		} finally {
			DatabaseFactory.close(con);
		}

		player.setEmotions(emotions);
	}

	/**
	 * @param player Player
	 */
	public static void insertEmotion(Player player, Emotion emotion)
	{
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
			stmt.setInt(1, player.getObjectId());
			stmt.setInt(2, emotion.getId());
			stmt.setInt(3, emotion.getExpireTime());
			stmt.execute();
			stmt.close();
		} catch (Exception e) {
			log.error("Could not store emotionId for player " + player.getObjectId() + " from DB: " + e.getMessage(), e);
		} finally {
			DatabaseFactory.close(con);
		}
	}

	public static void deleteEmotion(int playerId, int emotionId)
	{
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(DELETE_QUERY);
			stmt.setInt(1, playerId);
			stmt.setInt(2, emotionId);
			stmt.execute();
			stmt.close();
		} catch (Exception e) {
			log.error("Could not delete title for player " + playerId + " from DB: " + e.getMessage(), e);
		} finally {
			DatabaseFactory.close(con);
		}
	}
}
