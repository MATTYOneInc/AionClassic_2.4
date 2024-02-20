/*
 * This file is part of aion-unique <aionu-unique.org>.
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
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.gameserver.model.gameobjects.player.RecipeList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

/**
 * @author lord_rex
 */
public class PlayerRecipesDAO
{
	private static final String SELECT_QUERY = "SELECT `recipe_id` FROM player_recipes WHERE `player_id`=?";
	private static final String ADD_QUERY = "INSERT INTO player_recipes (`player_id`, `recipe_id`) VALUES (?, ?)";
	private static final String DELETE_QUERY = "DELETE FROM player_recipes WHERE `player_id`=? AND `recipe_id`=?";

	public static RecipeList load(final int playerId)
	{
		final HashSet<Integer> recipeList = new HashSet<Integer>();
		DB.select(SELECT_QUERY, new ParamReadStH()
		{
			@Override
			public void setParams(PreparedStatement ps) throws SQLException
			{
				ps.setInt(1, playerId);
			}

			@Override
			public void handleRead(ResultSet rs) throws SQLException
			{
				while (rs.next()) {
					recipeList.add(rs.getInt("recipe_id"));
				}
			}
		});

		return new RecipeList(recipeList);
	}

	public static boolean addRecipe(final int playerId, final int recipeId)
	{
		return DB.insertUpdate(ADD_QUERY, ps -> {
			ps.setInt(1, playerId);
			ps.setInt(2, recipeId);
			ps.execute();
		});
	}

	public static boolean delRecipe(final int playerId, final int recipeId)
	{
		return DB.insertUpdate(DELETE_QUERY, ps -> {
			ps.setInt(1, playerId);
			ps.setInt(2, recipeId);
			ps.execute();
		});
	}
}
