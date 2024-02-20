/*
 * This file is part of aion-lightning <aion-lightning.org>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author synchro2
 */
public class OldNamesDAO
{
	private static final Logger log = LoggerFactory.getLogger(OldNamesDAO.class);

	private static final String INSERT_QUERY = "INSERT INTO `old_names` (`player_id`, `old_name`, `new_name`) VALUES (?,?,?)";

	public static boolean isOldName(final String name)
	{
		PreparedStatement s = DB.prepareStatement("SELECT count(player_id) as cnt FROM old_names WHERE ? = old_names.old_name");
		try {
			s.setString(1, name);
			ResultSet rs = s.executeQuery();
			rs.next();
			return rs.getInt("cnt") > 0;
		} catch (SQLException e) {
			log.error("Can't check if name " + name + ", is used, returning possitive result", e);
			return true;
		} finally {
			DB.close(s);
		}
	}

	public static void insertNames(final int id, final String oldname, final String newname)
	{
		DB.insertUpdate(INSERT_QUERY, stmt -> {
			stmt.setInt(1, id);
			stmt.setString(2, oldname);
			stmt.setString(3, newname);
			stmt.execute();
		});
	}
}
