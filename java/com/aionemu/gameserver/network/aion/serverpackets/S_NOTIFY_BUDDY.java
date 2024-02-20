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
package com.aionemu.gameserver.network.aion.serverpackets;


import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * Notifies players when their friends log in, out, or delete them
 * 
 * @author Ben
 */
public class S_NOTIFY_BUDDY extends AionServerPacket {

	/**
	 * Buddy has logged in (Or become visible)
	 */
	public static final int LOGIN = 0;
	/**
	 * Buddy has logged out (Or become invisible)
	 */
	public static final int LOGOUT = 1;
	/**
	 * Buddy has deleted you
	 */
	public static final int DELETED = 2;

	private final int code;
	private final String name;

	/**
	 * Constructs a new notify packet
	 * 
	 * @param code
	 *          Message code
	 * @param name
	 *          Name of friend
	 */
	public S_NOTIFY_BUDDY(int code, String name) {
		this.code = code;
		this.name = name;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeS(name);
		writeC(code);
	}
}
