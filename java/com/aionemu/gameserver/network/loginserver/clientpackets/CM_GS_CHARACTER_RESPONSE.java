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
package com.aionemu.gameserver.network.loginserver.clientpackets;

import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.network.loginserver.LsClientPacket;
import com.aionemu.gameserver.network.loginserver.serverpackets.SM_GS_CHARACTER;

/**
 * @author cura
 */
public class CM_GS_CHARACTER_RESPONSE extends LsClientPacket {

	public CM_GS_CHARACTER_RESPONSE(int opCode) {
		super(opCode);
	}

	private int accountId;

	@Override
	public void readImpl() {
		accountId = readD();
	}

	@Override
	public void runImpl() {
		int characterCount = PlayerDAO.getCharacterCountOnAccount(accountId);
		sendPacket(new SM_GS_CHARACTER(accountId, characterCount));
	}
}
