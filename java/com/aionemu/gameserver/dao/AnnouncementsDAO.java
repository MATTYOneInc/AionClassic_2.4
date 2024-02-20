/*
 * MODIF EVO
 * Fichier de bannissement
 *
 * This file is part of aion-unique <aion-unique.org>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.DB;
import com.aionemu.gameserver.model.Announcement;

import java.util.HashSet;
import java.util.Set;

/**
 * DAO that manages Announcements
 *
 * @author Divinity
 */
public class AnnouncementsDAO
{
	public static Set<Announcement> getAnnouncements()
	{
		final Set<Announcement> result = new HashSet<Announcement>();
		DB.select("SELECT * FROM announcements ORDER BY id", resultSet -> {
			while (resultSet.next())
				result.add(new Announcement(resultSet.getInt("id"), resultSet.getString("announce"), resultSet.getString("faction"), resultSet.getString("type"), resultSet.getInt("delay")));
		});
		return result;
	}

	public static void addAnnouncement(final Announcement announce)
	{
		DB.insertUpdate("INSERT INTO announcements (announce, faction, type, delay) VALUES (?, ?, ?, ?)", preparedStatement -> {
			preparedStatement.setString(1, announce.getAnnounce());
			preparedStatement.setString(2, announce.getFaction());
			preparedStatement.setString(3, announce.getType());
			preparedStatement.setInt(4, announce.getDelay());
			preparedStatement.execute();
		});
	}

	public static boolean delAnnouncement(final int idAnnounce)
	{
		return DB.insertUpdate("DELETE FROM announcements WHERE id = ?", preparedStatement -> {
			preparedStatement.setInt(1, idAnnounce);
			preparedStatement.execute();
		});
	}
}
