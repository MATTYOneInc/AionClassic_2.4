/*
 * This file is part of aion-unique <aion-unique.org>.
 * <p>
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.model.tasks.TaskFromDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;

/**
 * @author Divinity
 */
public class TaskFromDBDAO
{
	private static final Logger log = LoggerFactory.getLogger(TaskFromDBDAO.class);
	private static final String SELECT_ALL_QUERY = "SELECT * FROM tasks ORDER BY id";
	private static final String UPDATE_QUERY = "UPDATE tasks SET last_activation = ? WHERE id = ?";

	/**
	 * Return all tasks from DB
	 *
	 * @return all tasks
	 */
	public static ArrayList<TaskFromDB> getAllTasks()
	{
		final ArrayList<TaskFromDB> result = new ArrayList<TaskFromDB>();

		Connection con = null;

		PreparedStatement stmt = null;
		try {
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(SELECT_ALL_QUERY);

			ResultSet rset = stmt.executeQuery();

			while (rset.next())
				result.add(new TaskFromDB(
						rset.getInt("id"),
						rset.getString("task"),
						rset.getString("type"),
						rset.getTimestamp("last_activation"),
						rset.getString("start_time"),
						rset.getInt("delay"),
						rset.getString("param"))
				);

			rset.close();
			stmt.close();
		} catch (SQLException e) {
			log.error("getAllTasks", e);
		} finally {
			DatabaseFactory.close(stmt, con);
		}

		return result;
	}

	/**
	 * Set the last activation to NOW()
	 */
	public static void setLastActivation(final int id)
	{
		Connection con = null;

		PreparedStatement stmt = null;
		try {
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(UPDATE_QUERY);

			stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			stmt.setInt(2, id);
			stmt.execute();
		} catch (SQLException e) {
			log.error("setLastActivation", e);
		} finally {
			DatabaseFactory.close(stmt, con);
		}
	}
}
