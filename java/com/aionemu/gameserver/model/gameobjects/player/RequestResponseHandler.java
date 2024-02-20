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
package com.aionemu.gameserver.model.gameobjects.player;

import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * Implemented by handlers of <tt>CM_QUESTION_RESPONSE</tt> responses
 * 
 * @author Ben
 * @modified Lyahim
 */
public abstract class RequestResponseHandler {

	private Creature requester;

	public RequestResponseHandler(Creature requester) {
		this.requester = requester;
	}

	/**
	 * Called when a response is received
	 * 
	 * @param requested
	 *          Player whom requested this response
	 * @param responder
	 *          Player whom responded to this request
	 * @param responseCode
	 *          The response the player gave, usually 0 = no 1 = yes
	 */
	public void handle(Player responder, int response) {
		if (response == 0)
			denyRequest(requester, responder);
		else
			acceptRequest(requester, responder);
	}

	/**
	 * Called when the player accepts a request
	 * 
	 * @param requester
	 *          Creature whom requested this response
	 * @param responder
	 *          Player whom responded to this request
	 */
	public abstract void acceptRequest(Creature requester, Player responder);

	/**
	 * Called when the player denies a request
	 * 
	 * @param requester
	 *          Creature whom requested this response
	 * @param responder
	 *          Player whom responded to this request
	 */
	public abstract void denyRequest(Creature requester, Player responder);

}
