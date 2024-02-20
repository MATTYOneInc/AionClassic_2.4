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
package com.aionemu.loginserver.dao;

import java.util.HashMap;
import java.util.Map;

import com.aionemu.commons.database.DB;
import com.aionemu.loginserver.GameServerInfo;

/**
 * DAO that manages GameServers
 *
 * @author -Nemesiss-
 * @author Skunk
 */
public class GameServersDAO
{
	/**
	 * Returns all gameservers from database.
	 *
	 * @return all gameservers from database.
	 */
	public static Map<Byte, GameServerInfo> getAllGameServers()
	{
		final Map<Byte, GameServerInfo> result = new HashMap<Byte, GameServerInfo>();
		DB.select("SELECT * FROM gameservers", resultSet -> {
			while (resultSet.next()) {
				byte id = resultSet.getByte("id");
				String ipMask = resultSet.getString("mask");
				String password = resultSet.getString("password");
				GameServerInfo gsi = new GameServerInfo(id, ipMask, password);
				result.put(id, gsi);
			}
		});

		return result;
	}
}
