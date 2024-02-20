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

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.gameserver.model.gameobjects.player.MacroList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Macrosses DAO
 * <p/>
 * Created on: 13.07.2009 17:05:56
 *
 * @author Aquanox
 */
public class PlayerMacrossesDAO
{
	private static Logger log = LoggerFactory.getLogger(PlayerMacrossesDAO.class);
	public static final String INSERT_QUERY = "INSERT INTO `player_macrosses` (`player_id`, `order`, `macro`) VALUES (?,?,?)";
	public static final String UPDATE_QUERY = "UPDATE `player_macrosses` SET `macro`=? WHERE `player_id`=? AND `order`=?";
	public static final String DELETE_QUERY = "DELETE FROM `player_macrosses` WHERE `player_id`=? AND `order`=?";
	public static final String SELECT_QUERY = "SELECT `order`, `macro` FROM `player_macrosses` WHERE `player_id`=?";

	/**
	 * Returns a list of macrosses for player
	 *
	 * @param playerId Player object id.
	 * @return a list of macrosses for player
	 */
	public static MacroList restoreMacrosses(final int playerId)
	{
		final Map<Integer, String> macrosses = new HashMap<Integer, String>();
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, playerId);
			ResultSet rset = stmt.executeQuery();
			log.debug("[DAO: PlayerMacrossesDAO] loading macroses for playerId: " + playerId);
			while (rset.next()) {
				int order = rset.getInt("order");
				String text = rset.getString("macro");
				macrosses.put(order, text);
			}
			rset.close();
			stmt.close();
		} catch (Exception e) {
			log.error("Could not restore MacroList data for player " + playerId + " from DB: " + e.getMessage(), e);
		} finally {
			DatabaseFactory.close(con);
		}

		return new MacroList(macrosses);
	}

	/**
	 * Add a macro information into database
	 *
	 * @param playerId      player object id
	 * @param macroPosition macro order # of player
	 * @param macro         macro contents.
	 */
	public static void addMacro(final int playerId, final int macroPosition, final String macro)
	{
		DB.insertUpdate(INSERT_QUERY, stmt -> {
			log.debug("[DAO: PlayerMacrossesDAO] storing macro " + playerId + " " + macroPosition);
			stmt.setInt(1, playerId);
			stmt.setInt(2, macroPosition);
			stmt.setString(3, macro);
			stmt.execute();
		});
	}

	/**
	 * Update a macro information into database
	 *
	 * @param playerId      player object id
	 * @param macroPosition macro order # of player
	 * @param macro         macro contents.
	 */
	public static void updateMacro(final int playerId, final int macroPosition, final String macro)
	{
		DB.insertUpdate(UPDATE_QUERY, stmt -> {
			log.debug("[DAO: PlayerMacrossesDAO] updating macro " + playerId + " " + macroPosition);
			stmt.setString(1, macro);
			stmt.setInt(2, playerId);
			stmt.setInt(3, macroPosition);
			stmt.execute();
		});
	}

	/**
	 * Remove macro in database
	 *
	 * @param playerId      player object id
	 * @param macroPosition order of macro in macro list
	 */
	public static void deleteMacro(final int playerId, final int macroPosition)
	{
		DB.insertUpdate(DELETE_QUERY, stmt -> {
			log.debug("[DAO: PlayerMacrossesDAO] removing macro " + playerId + " " + macroPosition);
			stmt.setInt(1, playerId);
			stmt.setInt(2, macroPosition);
			stmt.execute();
		});
	}
}
