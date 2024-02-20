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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.BlockedPlayer;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.SocialService;

/**
 * Developer's note please dont remove
 * Duoc goi khi block mot ai do va set memo cho nguoi do
 * @author Ben
 */
public class CM_BLOCK_SET_REASON extends AionClientPacket {

	String targetName;
	String reason;

	public CM_BLOCK_SET_REASON(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		targetName = readS();
		reason = readS();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Player activePlayer = getConnection().getActivePlayer();
		BlockedPlayer target = activePlayer.getBlockList().getBlockedPlayer(targetName);

		if (target == null)
			sendPacket(S_MESSAGE_CODE.STR_BLOCKLIST_NOT_IN_LIST);
		else {
			SocialService.setBlockedReason(activePlayer, target, reason);
		}
	}
}
