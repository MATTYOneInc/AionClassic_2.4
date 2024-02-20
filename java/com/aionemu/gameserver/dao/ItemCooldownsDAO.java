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
import com.aionemu.gameserver.model.items.ItemCooldown;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

/**
 * @author ATracer
 */
public class ItemCooldownsDAO
{
	private static final Logger log = LoggerFactory.getLogger(ItemCooldownsDAO.class);

	public static final String INSERT_QUERY = "INSERT INTO `item_cooldowns` (`player_id`, `delay_id`, `use_delay`, `reuse_time`) VALUES (?,?,?,?)";
	public static final String DELETE_QUERY = "DELETE FROM `item_cooldowns` WHERE `player_id`=?";
	public static final String SELECT_QUERY = "SELECT `delay_id`, `use_delay`, `reuse_time` FROM `item_cooldowns` WHERE `player_id`=?";

	private static final Predicate<ItemCooldown> itemCooldownPredicate = input -> input != null && input.getReuseTime() - System.currentTimeMillis() > 30000;

	/**
	 * @param player
	 */
	public static void loadItemCooldowns(final Player player)
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
					int delayId = rset.getInt("delay_id");
					int useDelay = rset.getInt("use_delay");
					long reuseTime = rset.getLong("reuse_time");

					if (reuseTime > System.currentTimeMillis())
						player.addItemCoolDown(delayId, reuseTime, useDelay);

				}
			}
		});

		player.getEffectController().broadCastEffects();
	}

	/**
	 * @param player
	 */
	public static void storeItemCooldowns(Player player)
	{
		deleteItemCooldowns(player);
		Map<Integer, ItemCooldown> itemCoolDowns = player.getItemCoolDowns();

		if (itemCoolDowns == null)
			return;

		Map<Integer, ItemCooldown> map = Maps.filterValues(itemCoolDowns, itemCooldownPredicate);
		final Iterator<Map.Entry<Integer, ItemCooldown>> iterator = map.entrySet().iterator();
		if (!iterator.hasNext()) {
			return;
		}

		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DatabaseFactory.getConnection();
			con.setAutoCommit(false);
			st = con.prepareStatement(INSERT_QUERY);

			while (iterator.hasNext()) {
				Map.Entry<Integer, ItemCooldown> entry = iterator.next();
				st.setInt(1, player.getObjectId());
				st.setInt(2, entry.getKey());
				st.setInt(3, entry.getValue().getUseDelay());
				st.setLong(4, entry.getValue().getReuseTime());
				st.addBatch();
			}

			st.executeBatch();
			con.commit();
		} catch (SQLException e) {
			log.error("Error while storing item cooldows for player " + player.getObjectId(), e);
		} finally {
			DatabaseFactory.close(st, con);
		}
	}

	private static void deleteItemCooldowns(final Player player)
	{
		DB.insertUpdate(DELETE_QUERY, stmt -> {
			stmt.setInt(1, player.getObjectId());
			stmt.execute();
		});
	}
}
