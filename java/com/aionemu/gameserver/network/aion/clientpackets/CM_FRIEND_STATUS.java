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

import com.aionemu.gameserver.model.gameobjects.player.FriendList.Status;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_FRIEND_STATUS extends AionClientPacket
{
	private final Logger log = LoggerFactory.getLogger(CM_FRIEND_STATUS.class);
	private byte status;
	
	public CM_FRIEND_STATUS(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		status = (byte) readC();
	}
	
	@Override
	protected void runImpl() {
		Player activePlayer = getConnection().getActivePlayer();
		Status statusEnum = Status.getByValue(status);
		if (statusEnum == null) {
			statusEnum = Status.ONLINE;
		}
		activePlayer.getFriendList().setStatus(statusEnum, activePlayer.getCommonData());
		//PacketSendUtility.sendPacket(activePlayer, new SM_FRIEND_STATUS(status));
	}
}