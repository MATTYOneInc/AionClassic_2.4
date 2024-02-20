/*
 *  Aion Classic Emu based on Aion Encom Source Files
 *
 *  ENCOM Team based on Aion-Lighting Open Source
 *  All Copyrights : "Data/Copyrights/AEmu-Copyrights.text
 *
 *  iMPERIVM.FUN - AION DEVELOPMENT FORUM
 *  Forum: <http://https://imperivm.fun/>
 *
 */
package com.aionemu.gameserver.dao;

import com.aionemu.commons.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author cura
 */
public class PlayerPasskeyDAO
{
	private static final Logger log = LoggerFactory.getLogger(PlayerPasskeyDAO.class);

	public static final String INSERT_QUERY = "INSERT INTO `player_passkey` (`account_id`, `passkey`) VALUES (?,?)";
	public static final String UPDATE_QUERY = "UPDATE `player_passkey` SET `passkey`=? WHERE `account_id`=? AND `passkey`=?";
	public static final String UPDATE_FORCE_QUERY = "UPDATE `player_passkey` SET `passkey`=? WHERE `account_id`=?";
	public static final String CHECK_QUERY = "SELECT COUNT(*) cnt FROM `player_passkey` WHERE `account_id`=? AND `passkey`=?";
	public static final String EXIST_CHECK_QUERY = "SELECT COUNT(*) cnt FROM `player_passkey` WHERE `account_id`=?";

	/**
	 * @param accountId
	 * @param passkey
	 */
	public static void insertPlayerPasskey(int accountId, String passkey)
	{
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
			stmt.setInt(1, accountId);
			stmt.setString(2, passkey);
			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			log.error("Error saving PlayerPasskey. accountId: " + accountId, e);
		} finally {
			DatabaseFactory.close(con);
		}
	}

	/**
	 * @param accountId
	 * @param oldPasskey
	 * @param newPasskey
	 * @return
	 */
	public static boolean updatePlayerPasskey(int accountId, String oldPasskey, String newPasskey)
	{
		boolean result = false;
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
			stmt.setString(1, newPasskey);
			stmt.setInt(2, accountId);
			stmt.setString(3, oldPasskey);
			if (stmt.executeUpdate() > 0) {
				result = true;
			}
			stmt.close();
		} catch (SQLException e) {
			log.error("Error updating PlayerPasskey. accountId: " + accountId, e);
		} finally {
			DatabaseFactory.close(con);
		}

		return result;
	}

	/**
	 * @param accountId
	 * @param newPasskey
	 * @return
	 */
	public static boolean updateForcePlayerPasskey(int accountId, String newPasskey)
	{
		boolean result = false;
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(UPDATE_FORCE_QUERY);
			stmt.setString(1, newPasskey);
			stmt.setInt(2, accountId);
			if (stmt.executeUpdate() > 0) {
				result = true;
			}
			stmt.close();
		} catch (SQLException e) {
			log.error("Error updaing PlayerPasskey. accountId: " + accountId, e);
		} finally {
			DatabaseFactory.close(con);
		}

		return result;
	}

	/**
	 * @param accountId
	 * @param passkey
	 * @return
	 */
	public static boolean checkPlayerPasskey(int accountId, String passkey)
	{
		boolean passkeyChecked = false;
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(CHECK_QUERY);
			stmt.setInt(1, accountId);
			stmt.setString(2, passkey);
			ResultSet rset = stmt.executeQuery();
			if (rset.next()) {
				if (rset.getInt("cnt") == 1) {
					passkeyChecked = true;
				}
			}
			rset.close();
			stmt.close();
		} catch (SQLException e) {
			log.error("Error loading PlayerPasskey. accountId: " + accountId, e);
			return false;
		} finally {
			DatabaseFactory.close(con);
		}

		return passkeyChecked;
	}

	/**
	 * @param accountId
	 * @return
	 */
	public static boolean existCheckPlayerPasskey(int accountId)
	{
		boolean existPasskeyChecked = false;
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(EXIST_CHECK_QUERY);
			stmt.setInt(1, accountId);
			ResultSet rset = stmt.executeQuery();
			if (rset.next()) {
				if (rset.getInt("cnt") == 1) {
					existPasskeyChecked = true;
				}
			}
			rset.close();
			stmt.close();
		} catch (SQLException e) {
			log.error("Error loading PlayerPasskey. accountId: " + accountId, e);
			return false;
		} finally {
			DatabaseFactory.close(con);
		}

		return existPasskeyChecked;
	}
}
