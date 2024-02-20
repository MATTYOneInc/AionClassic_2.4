/*
 * This file is part of aion-emu <aion-emu.com>.
 * <p>
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.loginserver.dao;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.loginserver.model.BannedIP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO that manages Banned IPs
 *
 * @author SoulKeeper
 */
public class BannedIpDAO
{
	private static final Logger log = LoggerFactory.getLogger(BannedIpDAO.class);

	/**
	 * Inserts ip mask to database, returns BannedIP object that represents inserted mask or null if error.<br>
	 * Expire time is null so ban never expires.<br>
	 *
	 * @param mask ip mask to ban
	 * @return BannedIP object represetns mask or null if error happened
	 */
	public static BannedIP insert(String mask)
	{
		return insert(mask, null);
	}

	/**
	 * Inserts ip mask to database with given expireTime.<br>
	 * Null is allowed for expire time in case of infinite ban.<br>
	 * Returns object that represents ip mask or null in case of error.<br>
	 *
	 * @param mask ip mask to ban
	 * @param expireTime expiration time of ban
	 * @return object that represetns added ban or null in case of error
	 */
	public static BannedIP insert(final String mask, final Timestamp expireTime)
	{
		BannedIP result = new BannedIP();
		result.setMask(mask);
		result.setTimeEnd(expireTime);

		if (insert(result)) {
			return result;
		}
		return null;
	}

	/**
	 * Inserts BannedIP object to database.<br>
	 * ID of object must be NULL.<br>
	 * If insert was successfull - sets the assigned id to BannedIP object and returns true.<br>
	 * In case of error returns false without modification of bannedIP object.<br>
	 *
	 * @param bannedIP record to add to db
	 * @return true in case of success or false
	 */
	public static boolean insert(final BannedIP bannedIP)
	{
		boolean insert = DB.insertUpdate("INSERT INTO banned_ip(mask, time_end) VALUES (?, ?)", preparedStatement -> {
			preparedStatement.setString(1, bannedIP.getMask());
			if (bannedIP.getTimeEnd() == null)
				preparedStatement.setNull(2, Types.TIMESTAMP);
			else
				preparedStatement.setTimestamp(2, bannedIP.getTimeEnd());
			preparedStatement.execute();
		});

		if (!insert)
			return false;

		final BannedIP result = new BannedIP();
		DB.select("SELECT * FROM banned_ip WHERE mask = ?", new ParamReadStH()
		{

			@Override
			public void setParams(PreparedStatement preparedStatement) throws SQLException
			{
				preparedStatement.setString(1, bannedIP.getMask());
			}

			@Override
			public void handleRead(ResultSet resultSet) throws SQLException
			{
				resultSet.next(); // mask is unique, only one result allowed
				result.setId(resultSet.getInt("id"));
				result.setMask(resultSet.getString("mask"));
				result.setTimeEnd(resultSet.getTimestamp("time_end"));
			}
		});
		return true;
	}

	/**
	 * Updates BannedIP object.<br>
	 * ID of object must NOT be null.<br>
	 * In case of success returns true.<br>
	 * In case of error returns false.<br>
	 *
	 * @param bannedIP record to update
	 * @return true in case of success or false in other case
	 */
	public static boolean update(final BannedIP bannedIP)
	{
		return DB.insertUpdate("UPDATE banned_ip SET mask = ?, time_end = ? WHERE id = ?", preparedStatement -> {
			preparedStatement.setString(1, bannedIP.getMask());
			if (bannedIP.getTimeEnd() == null)
				preparedStatement.setNull(2, Types.TIMESTAMP);
			else
				preparedStatement.setTimestamp(2, bannedIP.getTimeEnd());
			preparedStatement.setInt(3, bannedIP.getId());
			preparedStatement.execute();
		});
	}

	/**
	 * Removes ban by mask.<br>
	 * Returns true in case of success, false othervise.<br>
	 *
	 * @param mask ip mask to remove
	 * @return true in case of success, false in other case
	 */
	public static boolean remove(final String mask)
	{
		return DB.insertUpdate("DELETE FROM banned_ip WHERE mask = ?", preparedStatement -> {
			preparedStatement.setString(1, mask);
			preparedStatement.execute();
		});
	}

	/**
	 * Removes BannedIP record by ID. Id must not be null.<br>
	 * Returns true in case of success, false in case of error
	 *
	 * @param bannedIP record to unban
	 * @return true if removeas wass successfull, false in case of error
	 */
	public static boolean remove(final BannedIP bannedIP)
	{
		return DB.insertUpdate("DELETE FROM banned_ip WHERE mask = ?", preparedStatement -> {
			// Changed from id to mask because we don't get id of last inserted ban
			preparedStatement.setString(1, bannedIP.getMask());
			preparedStatement.execute();
		});
	}

	public static void cleanExpiredBans()
	{
		DB.insertUpdate("DELETE FROM banned_ip WHERE time_end < current_timestamp AND time_end IS NOT NULL");
	}

	/**
	 * Returns all bans from database.
	 *
	 * @return all bans from database.
	 */
	public static Set<BannedIP> getAllBans()
	{
		final Set<BannedIP> result = new HashSet<BannedIP>();
		DB.select("SELECT * FROM banned_ip", resultSet -> {
			while (resultSet.next()) {
				BannedIP ip = new BannedIP();
				ip.setId(resultSet.getInt("id"));
				ip.setMask(resultSet.getString("mask"));
				ip.setTimeEnd(resultSet.getTimestamp("time_end"));
				result.add(ip);
			}
		});
		return result;
	}
}
