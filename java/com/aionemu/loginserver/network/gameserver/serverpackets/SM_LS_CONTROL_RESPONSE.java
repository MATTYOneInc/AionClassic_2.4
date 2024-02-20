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

import java.sql.Timestamp;


/**
 * @author Aionchs-Wylovech
 */
public class SM_LS_CONTROL_RESPONSE extends GsServerPacket {

	private int type;

	private boolean result;

	private String playerName;

	private int param;
	private long vipexpire;

	private String adminName;

	private int accountId;

	public SM_LS_CONTROL_RESPONSE(int type, boolean result, String playerName, int accountId, int param, String adminName, long vipExpire) {
		this.type = type;
		this.result = result;
		this.playerName = playerName;
		this.param = param;
		this.adminName = adminName;
		this.accountId = accountId;
		this.vipexpire = vipExpire;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(GsConnection con) {
		writeC(4);
		writeC(type);
		writeC(result ? 1 : 0);
		writeS(adminName);
		writeS(playerName);
		writeC(param);
		writeQ(vipexpire);
		writeD(accountId);
	}
}
