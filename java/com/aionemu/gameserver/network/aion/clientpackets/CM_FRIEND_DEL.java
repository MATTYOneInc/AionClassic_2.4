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

import com.aionemu.gameserver.model.gameobjects.player.Friend;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.SocialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_FRIEND_DEL extends AionClientPacket
{
	private String targetName;
	private static Logger log = LoggerFactory.getLogger(CM_FRIEND_DEL.class);
	
	public CM_FRIEND_DEL(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		targetName = readS();
	}
	
	@Override
	protected void runImpl() {
		Player activePlayer = getConnection().getActivePlayer();
		Friend target = activePlayer.getFriendList().getFriend(targetName);
		if (target == null) {
			sendPacket(S_MESSAGE_CODE.STR_BUDDYLIST_NOT_IN_LIST);
		} else {
			SocialService.deleteFriend(activePlayer, target.getOid());
		}
	}
}