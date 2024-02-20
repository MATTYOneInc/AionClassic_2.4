/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author nrg
 */
public class PlayerCooldownsDAO
{
	private static final Logger log = LoggerFactory.getLogger(PlayerCooldownsDAO.class);

	public static final String INSERT_QUERY = "INSERT INTO `player_cooldowns` (`player_id`, `cooldown_id`, `reuse_delay`) VALUES (?,?,?)";
	public static final String DELETE_QUERY = "DELETE FROM `player_cooldowns` WHERE `player_id`=?";
	public static final String SELECT_QUERY = "SELECT `cooldown_id`, `reuse_delay` FROM `player_cooldowns` WHERE `player_id`=?";

	private static final Predicate<Long> cooldownPredicate = input -> input != null && input - System.currentTimeMillis() > 28000;

	/**
	 * @param player Player
	 */
	public static void loadPlayerCooldowns(final Player player)
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
					int cooldownId = rset.getInt("cooldown_id");
					long reuseDelay = rset.getLong("reuse_delay");

					if (reuseDelay > System.currentTimeMillis())
						player.setSkillCoolDown(cooldownId, reuseDelay);
				}
			}
		});
	}

	/**
	 * @param player Player
	 */
	public static void storePlayerCooldowns(final Player player)
	{
		deletePlayerCooldowns(player);

		Map<Integer, Long> cooldowns = player.getSkillCoolDowns();
		if (cooldowns != null && cooldowns.size() > 0) {
			Map<Integer, Long> filteredCooldown = Maps.filterValues(cooldowns, cooldownPredicate);

			if (filteredCooldown.isEmpty()) {
				return;
			}

			Connection con = null;
			PreparedStatement st = null;
			try {
				con = DatabaseFactory.getConnection();
				con.setAutoCommit(false);
				st = con.prepareStatement(INSERT_QUERY);

				for (Map.Entry<Integer, Long> entry : filteredCooldown.entrySet()) {
					st.setInt(1, player.getObjectId());
					st.setInt(2, entry.getKey());
					st.setLong(3, entry.getValue());
					st.addBatch();
				}

				st.executeBatch();
				con.commit();

			} catch (SQLException e) {
				log.error("Can't save cooldowns for player " + player.getObjectId());
			} finally {
				DatabaseFactory.close(st, con);
			}
		}
	}

	/**
	 * @param player Player
	 */
	private static void deletePlayerCooldowns(final Player player)
	{
		DB.insertUpdate(DELETE_QUERY, stmt -> {
			stmt.setInt(1, player.getObjectId());
			stmt.execute();
		});
	}
}
