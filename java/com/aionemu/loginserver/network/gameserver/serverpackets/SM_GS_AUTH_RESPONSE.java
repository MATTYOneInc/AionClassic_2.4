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

import com.aionemu.loginserver.GameServerTable;
import com.aionemu.loginserver.network.gameserver.GsAuthResponse;
import com.aionemu.loginserver.network.gameserver.GsConnection;
import com.aionemu.loginserver.network.gameserver.GsServerPacket;

/**
 * This packet is response for CM_GS_AUTH its notify Gameserver if registration was ok or what was wrong.
 * 
 * @author -Nemesiss-
 */
public class SM_GS_AUTH_RESPONSE extends GsServerPacket {

	/**
	 * Response for Gameserver authentication
	 */
	private final GsAuthResponse response;

	/**
	 * Constructor.
	 * 
	 * @param response
	 */
	public SM_GS_AUTH_RESPONSE(GsAuthResponse response) {
		this.response = response;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(GsConnection con) {
		writeC(0);
		writeC(response.getResponseId());
		if(response.getResponseId() == 0)
			writeC(GameServerTable.getGameServers().size());
	}
}
