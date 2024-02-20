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

import com.aionemu.commons.database.DB;
import com.aionemu.loginserver.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO that manages accounts.
 *
 * @author SoulKeeper
 * @author Skunk
 */
public class AccountDAO
{
	private static final Logger log = LoggerFactory.getLogger(AccountDAO.class);

	/**
	 * Returns account by name or null
	 *
	 * @param name account name
	 *
	 * @return account object or null
	 */
	public static Account getAccount(String name)
	{
		Account account = null;
		PreparedStatement st = DB.prepareStatement("SELECT * FROM account_data WHERE `name` = ?");

		try {
			st.setString(1, name);

			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				account = new Account();

				account.setId(rs.getInt("id"));
				account.setName(name);
				account.setPasswordHash(rs.getString("password"));
				account.setAccessLevel(rs.getByte("access_level"));
				account.setMembership(rs.getByte("membership"));
				account.setActivated(rs.getByte("activated"));
				account.setLastServer(rs.getByte("last_server"));
				account.setLastIp(rs.getString("last_ip"));
				account.setLastMac(rs.getString("last_mac"));
				account.setIpForce(rs.getString("ip_force"));
				account.setMembershipExpire(rs.getTimestamp("membership_expire"));
			}
		} catch (Exception e) {
			log.error("Can't select account with name: " + name, e);
		} finally {
			DB.close(st);
		}

		return account;
	}

	/**
	 * Get Account by Token
	 *
	 * @param token string
	 * @return Account or null
	 */
	public static Account getAccountByToken(String token)
	{
		Account account = null;
		PreparedStatement st = DB.prepareStatement("SELECT * FROM account_data WHERE `auth_key` = ?");

		try {
			st.setString(1, token);

			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				account = new Account();

				account.setId(rs.getInt("id"));
				account.setName(rs.getString("name"));
				account.setPasswordHash(rs.getString("password"));
				account.setAccessLevel(rs.getByte("access_level"));
				account.setMembership(rs.getByte("membership"));
				account.setActivated(rs.getByte("activated"));
				account.setLastServer(rs.getByte("last_server"));
				account.setLastIp(rs.getString("last_ip"));
				account.setLastMac(rs.getString("last_mac"));
				account.setIpForce(rs.getString("ip_force"));
				account.setMembershipExpire(rs.getTimestamp("membership_expire"));
			}
		} catch (Exception e) {
			log.error("Can't select account with token: " + token, e);
		} finally {
			DB.close(st);
		}

		return account;
	}

	/**
	 * Returns Account by ID
	 *
	 * @param id AccountID
	 * @return Account
	 */
	public static Account getAccount(int id)
	{
		Account account = null;
		PreparedStatement st = DB.prepareStatement("SELECT * FROM account_data WHERE `id` = ?");

		try {
			st.setInt(1, id);

			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				account = new Account();

				account.setId(rs.getInt("id"));
				account.setName(rs.getString("name"));
				account.setPasswordHash(rs.getString("password"));
				account.setAccessLevel(rs.getByte("access_level"));
				account.setMembership(rs.getByte("membership"));
				account.setActivated(rs.getByte("activated"));
				account.setLastServer(rs.getByte("last_server"));
				account.setLastIp(rs.getString("last_ip"));
				account.setLastMac(rs.getString("last_mac"));
				account.setIpForce(rs.getString("ip_force"));
				account.setMembershipExpire(rs.getTimestamp("membership_expire"));
			}
		} catch (Exception e) {
			log.error("Can't select account with name: " + id, e);
		} finally {
			DB.close(st);
		}

		return account;
	}

	/**
	 * Returns account by player name and his password
	 *
	 * @param playerName
	 * @param hashPass
	 * @return int accessLevel
	 */
	public static int getPlayerAccess(String playerName, String hashPass)
	{
		int accessLevel = -1;
		PreparedStatement st = DB.prepareStatement("SELECT access_level FROM players P, account_data AD WHERE P.account_name = AD.name AND P.name = ? AND AD.password = ?");

		try {
			st.setString(1, playerName);
			st.setString(2, hashPass);

			ResultSet rs = st.executeQuery();

			if (rs.next())
				accessLevel = rs.getInt("access_level");
		} catch (Exception e) {
			log.error("Player account information could not be found for player: " + playerName, e);
		} finally {
			DB.close(st);
		}

		return accessLevel;
	}

	/**
	 * Returns account id or -1 in case of error
	 *
	 * @param name name of account
	 * @return id or -1 in case of error
	 */
	public static int getAccountId(String name)
	{
		int id = -1;
		PreparedStatement st = DB.prepareStatement("SELECT `id` FROM account_data WHERE `name` = ?");

		try {
			st.setString(1, name);

			ResultSet rs = st.executeQuery();

			rs.next();

			id = rs.getInt("id");
		} catch (SQLException e) {
			log.error("Can't select id after account insertion", e);
		} finally {
			DB.close(st);
		}

		return id;
	}

	/**
	 * Return account count If error occurred - returns -1
	 *
	 * @return account count
	 */
	public static int getAccountCount()
	{
		PreparedStatement st = DB.prepareStatement("SELECT count(*) AS c FROM account_data");
		ResultSet rs = DB.executeQuerry(st);

		try {
			rs.next();

			return rs.getInt("c");
		} catch (SQLException e) {
			log.error("Can't get account count", e);
		} finally {
			DB.close(st);
		}

		return -1;
	}

	/**
	 * Insert new account to database. Sets account ID to id that was generated by DB.
	 *
	 * @param account account to insert
	 *
	 * @return true if was inserted, false in other case
	 */
	public static boolean insertAccount(Account account)
	{
		System.out.println("Call insertAccount");
		int result = 0;
		PreparedStatement st = DB.prepareStatement("INSERT INTO account_data(`name`, `password`, access_level, membership, activated, last_server, last_ip, last_mac, ip_force, toll) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		try {
			st.setString(1, account.getName());
			st.setString(2, account.getPasswordHash());
			st.setByte(3, account.getAccessLevel());
			st.setByte(4, account.getMembership());
			st.setByte(5, account.getActivated());
			st.setByte(6, account.getLastServer());
			st.setString(7, account.getLastIp());
			st.setString(8, account.getLastMac());
			st.setString(9, account.getIpForce());
			st.setLong(10, 0);
			result = st.executeUpdate();
		} catch (SQLException e) {
			log.error("Can't inser account", e);
		} finally {
			DB.close(st);
		}

		if (result > 0) {
			account.setId(getAccountId(account.getName()));
		}

		return result > 0;
	}

	/**
	 * Updates account in database
	 *
	 * @param account account to update
	 *
	 * @return true if was updated, false in other case
	 */
	public static boolean updateAccount(Account account)
	{
		int result = 0;
		PreparedStatement st = DB.prepareStatement("UPDATE account_data SET `name` = ?, `password` = ?, access_level = ?, membership = ?, last_server = ?, last_ip = ?, last_mac = ?, ip_force = ?, membership_expire=? WHERE `id` = ?");

		try {
			st.setString(1, account.getName());
			st.setString(2, account.getPasswordHash());
			st.setByte(3, account.getAccessLevel());
			st.setByte(4, account.getMembership());
			st.setByte(5, account.getLastServer());
			st.setString(6, account.getLastIp());
			st.setString(7, account.getLastMac());
			st.setString(8, account.getIpForce());
			st.setTimestamp(9, account.getMembershipExpire());
			st.setInt(10, account.getId());
			st.executeUpdate();
		} catch (SQLException e) {
			log.error("Can't update account");
		} finally {
			DB.close(st);
		}

		return result > 0;
	}

	/**
	 * Updates lastServer field of account
	 *
	 * @param accountId  account id
	 * @param lastServer last accessed server
	 * @return was updated successful or not
	 */
	public static boolean updateLastServer(final int accountId, final byte lastServer)
	{
		return DB.insertUpdate("UPDATE account_data SET last_server = ? WHERE id = ?", preparedStatement -> {
			preparedStatement.setByte(1, lastServer);
			preparedStatement.setInt(2, accountId);
			preparedStatement.execute();
		});
	}

	/**
	 * Updates last ip that was used to access an account
	 *
	 * @param accountId account id
	 * @param ip        ip address
	 * @return was update successful or not
	 */
	public static boolean updateLastIp(final int accountId, final String ip)
	{
		return DB.insertUpdate("UPDATE account_data SET last_ip = ? WHERE id = ?", preparedStatement -> {
			preparedStatement.setString(1, ip);
			preparedStatement.setInt(2, accountId);
			preparedStatement.execute();
		});
	}

	/**
	 * Get last ip that was used to access an account
	 *
	 * @param accountId account id
	 * @return ip address
	 */
	public static String getLastIp(final int accountId)
	{
		String lastIp = "";
		PreparedStatement st = DB.prepareStatement("SELECT `last_ip` FROM `account_data` WHERE `id` = ?");

		try {
			st.setInt(1, accountId);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				lastIp = rs.getString("last_ip");
			}
		} catch (Exception e) {
			log.error("Can't select last IP of account ID: " + accountId, e);
			return "";
		} finally {
			DB.close(st);
		}

		return lastIp;
	}

	/**
	 * Updates last mac that was used to access an account
	 *
	 * @param accountId account id
	 * @param mac       mac address
	 *
	 * @return was update successful or not
	 */
	public static boolean updateLastMac(final int accountId, final String mac)
	{
		return DB.insertUpdate("UPDATE `account_data` SET `last_mac` = ? WHERE `id` = ?", preparedStatement -> {
			preparedStatement.setString(1, mac);
			preparedStatement.setInt(2, accountId);
			preparedStatement.execute();
		});
	}

	/**
	 * Updates account membership
	 *
	 * @param accountId account id
	 * @return was update successful or not
	 */
	public static boolean updateMembership(final int accountId)
	{
		return DB.insertUpdate(
				"UPDATE account_data SET membership = old_membership, membership_expire = NULL WHERE id = ? and membership_expire < CURRENT_TIMESTAMP",
				preparedStatement -> {
					preparedStatement.setInt(1, accountId);
					preparedStatement.execute();
				});
	}
}
