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

package com.aionemu.gameserver.network.loginserver.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.loginserver.LoginServerConnection;
import com.aionemu.gameserver.network.loginserver.LsServerPacket;

import java.util.Map;

/**
 * GameServer packet that sends list of logged in accounts
 * 
 * @author SoulKeeper
 */
public class SM_ACCOUNT_LIST extends LsServerPacket {

	/**
	 * Map with loaded accounts
	 */
	private final Map<Integer, AionConnection> accounts;

	/**
	 * constructs new server packet with specified opcode.
	 */
	public SM_ACCOUNT_LIST(Map<Integer, AionConnection> accounts) {
		super(0x04);
		this.accounts = accounts;
	}

	@Override
	protected void writeImpl(LoginServerConnection con) {
		writeD(accounts.size());
		for (AionConnection ac : accounts.values()) {
			writeS(ac.getAccount().getName());
		}
	}
}
