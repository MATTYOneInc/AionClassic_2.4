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

import com.aionemu.loginserver.network.aion.AionAuthResponse;
import com.aionemu.loginserver.network.aion.AionServerPacket;
import com.aionemu.loginserver.network.aion.LoginConnection;

/**
 * @author KID
 */
public class SM_LOGIN_FAIL extends AionServerPacket {

	/**
	 * response - why login fail
	 */
	private AionAuthResponse response;

	/**
	 * Constructs new instance of <tt>SM_LOGIN_FAIL</tt> packet.
	 * 
	 * @param response
	 *          auth responce
	 */
	public SM_LOGIN_FAIL(AionAuthResponse response) {
		super(0x01);
		this.response = response;
	}

	@Override
	protected void writeImpl(LoginConnection con) {
		writeC(getOpcode());
		writeD(response.getMessageId());
	}
}
