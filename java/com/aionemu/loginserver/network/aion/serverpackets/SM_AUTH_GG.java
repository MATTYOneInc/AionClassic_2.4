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
package com.aionemu.loginserver.network.aion.serverpackets;

import com.aionemu.loginserver.network.aion.AionServerPacket;
import com.aionemu.loginserver.network.aion.LoginConnection;

/**
 * @author -Nemesiss-
 */
public class SM_AUTH_GG extends AionServerPacket {

	/**
	 * Session Id of this connection
	 */
	private final int sessionId;

	/**
	 * Constructs new instance of <tt>SM_AUTH_GG</tt> packet
	 * 
	 * @param sessionId
	 */
	public SM_AUTH_GG(int sessionId) {
		super(0x0b);

		this.sessionId = sessionId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(LoginConnection con) {
		writeC(getOpcode());
		writeD(sessionId);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
		writeB(new byte[0x13]);
	}
}
