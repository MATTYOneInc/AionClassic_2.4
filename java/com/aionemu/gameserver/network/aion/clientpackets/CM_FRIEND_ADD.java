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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.DeniedStatus;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_BUDDY_RESULT;
import com.aionemu.gameserver.network.aion.serverpackets.S_ASK;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.SocialService;
import com.aionemu.gameserver.world.World;

/**
 * Received when a user tries to add someone as his friend
 * 
 * @author Ben
 */
public class CM_FRIEND_ADD extends AionClientPacket {

	private String targetName;

	public CM_FRIEND_ADD(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		targetName = readS();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {

		final Player activePlayer = getConnection().getActivePlayer();
		final Player targetPlayer = World.getInstance().findPlayer(targetName);

		if (targetName.equalsIgnoreCase(activePlayer.getName())) {
			// Adding self to friend list not allowed - Its blocked by the client by default, so no need to send an error
		}
		// if offline
		else if (targetPlayer == null) {
			sendPacket(new S_BUDDY_RESULT(targetName, S_BUDDY_RESULT.TARGET_OFFLINE));
		}
		else if (activePlayer.getFriendList().getFriend(targetPlayer.getObjectId()) != null) {
			sendPacket(new S_BUDDY_RESULT(targetPlayer.getName(), S_BUDDY_RESULT.TARGET_ALREADY_FRIEND));
		}
		else if (activePlayer.getFriendList().isFull()) {
			sendPacket(S_MESSAGE_CODE.STR_BUDDYLIST_LIST_FULL);
		}
		else if (activePlayer.getCommonData().getRace() != targetPlayer.getCommonData().getRace()) {
			sendPacket(new S_BUDDY_RESULT(targetPlayer.getName(), S_BUDDY_RESULT.TARGET_NOT_FOUND));
		}
		else if (targetPlayer.getFriendList().isFull()) {
			sendPacket(new S_BUDDY_RESULT(targetPlayer.getName(), S_BUDDY_RESULT.TARGET_LIST_FULL));
		}
		else if (activePlayer.getBlockList().contains(targetPlayer.getObjectId())) {
			sendPacket(new S_BUDDY_RESULT(targetPlayer.getName(), S_BUDDY_RESULT.TARGET_BLOCKED));
		}
		else if (targetPlayer.getBlockList().contains(activePlayer.getObjectId())) {
			sendPacket(S_MESSAGE_CODE.STR_YOU_EXCLUDED(targetName));
		}
		else // Send request
		{
			RequestResponseHandler responseHandler = new RequestResponseHandler(activePlayer) {

				@Override
				public void acceptRequest(Creature requester, Player responder) {
					if (!targetPlayer.getCommonData().isOnline()) {
						sendPacket(new S_BUDDY_RESULT(targetName, S_BUDDY_RESULT.TARGET_OFFLINE));
					}
					else if (activePlayer.getFriendList().isFull() || responder.getFriendList().isFull()) {
						return;
					}
					else {
						SocialService.makeFriends((Player) requester, responder);
					}

				}

				@Override
				public void denyRequest(Creature requester, Player responder) {
					sendPacket(new S_BUDDY_RESULT(targetName, S_BUDDY_RESULT.TARGET_DENIED));

				}
			};

			boolean requested = targetPlayer.getResponseRequester().putRequest(
				S_ASK.STR_BUDDYLIST_ADD_BUDDY_REQUEST, responseHandler);
			// If the player is busy and could not be asked
			if (!requested) {
				sendPacket(S_MESSAGE_CODE.STR_BUDDYLIST_BUSY);
			}
			else {
				if (targetPlayer.getPlayerSettings().isInDeniedStatus(DeniedStatus.FRIEND)) {
					sendPacket(S_MESSAGE_CODE.STR_MSG_REJECTED_FRIEND(targetPlayer.getName()));
					return;
				}
				// Send question packet to buddy
				targetPlayer.getClientConnection().sendPacket(new S_ASK(S_ASK.STR_BUDDYLIST_ADD_BUDDY_REQUEST, activePlayer.getObjectId(), 0, activePlayer.getName()));
			}
		}
	}

}
