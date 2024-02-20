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
package com.aionemu.loginserver.network.gameserver.serverpackets;

import com.aionemu.loginserver.network.gameserver.GsConnection;
import com.aionemu.loginserver.network.gameserver.GsServerPacket;


/**
 * @author cura
 */
public class SM_GS_CHARACTER_RESPONSE extends GsServerPacket {

	private final int accountId;

	/**
	 * @param accountId
	 */
	public SM_GS_CHARACTER_RESPONSE(int accountId) {
		this.accountId = accountId;
	}

	@Override
	protected void writeImpl(GsConnection con) {
		writeC(8);
		writeD(accountId);
	}
}
