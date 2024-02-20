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
import com.aionemu.gameserver.skillengine.model.Effect;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * @author ATracer
 */
public class PlayerEffectsDAO
{
	private static final Logger log = LoggerFactory.getLogger(PlayerEffectsDAO.class);

	public static final String INSERT_QUERY = "INSERT INTO `player_effects` (`player_id`, `skill_id`, `skill_lvl`, `current_time`, `end_time`) VALUES (?,?,?,?,?)";
	public static final String DELETE_QUERY = "DELETE FROM `player_effects` WHERE `player_id`=?";
	public static final String SELECT_QUERY = "SELECT `skill_id`, `skill_lvl`, `current_time`, `end_time` FROM `player_effects` WHERE `player_id`=?";

	private static final Predicate<Effect> insertableEffectsPredicate = input -> input != null && input.getRemainingTime() > 28000 && input.getEndTime() != 0;

	/**
	 * @param player Player
	 */
	public static void loadPlayerEffects(final Player player)
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
					int skillId = rset.getInt("skill_id");
					int skillLvl = rset.getInt("skill_lvl");
					int remainingTime = rset.getInt("current_time");
					long endTime = rset.getLong("end_time");

					if (remainingTime > 0)
						player.getEffectController().addSavedEffect(skillId, skillLvl, remainingTime, endTime);
				}
			}
		});

		player.getEffectController().broadCastEffects();
	}

	/**
	 * @param player Player
	 */
	public static void storePlayerEffects(final Player player)
	{
		deletePlayerEffects(player);

		Iterator<Effect> iterator = player.getEffectController().iterator();
		iterator = Iterators.filter(iterator, insertableEffectsPredicate);

		if (!iterator.hasNext()) {
			return;
		}

		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = DatabaseFactory.getConnection();
			con.setAutoCommit(false);
			ps = con.prepareStatement(INSERT_QUERY);

			while (iterator.hasNext()) {
				Effect effect = iterator.next();
				ps.setInt(1, player.getObjectId());
				ps.setInt(2, effect.getSkillId());
				ps.setInt(3, effect.getSkillLevel());
				ps.setInt(4, effect.getRemainingTime());
				ps.setLong(5, effect.getEndTime());
				ps.addBatch();
			}

			ps.executeBatch();
			con.commit();
		} catch (SQLException e) {
			log.error("Exception while saving effects of player " + player.getObjectId(), e);
		} finally {
			DatabaseFactory.close(ps, con);
		}
	}

	/**
	 * @param player Player
	 */
	private static void deletePlayerEffects(final Player player)
	{
		DB.insertUpdate(DELETE_QUERY, stmt -> {
			stmt.setInt(1, player.getObjectId());
			stmt.execute();
		});
	}
}
