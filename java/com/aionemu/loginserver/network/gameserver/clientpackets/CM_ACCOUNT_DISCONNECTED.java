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

package com.aionemu.loginserver.network.gameserver.clientpackets;

import com.aionemu.loginserver.controller.AccountTimeController;
import com.aionemu.loginserver.model.Account;
import com.aionemu.loginserver.network.gameserver.GsClientPacket;

/**
 * In this packet GameServer is informing LoginServer that some account is no longer on GameServer [ie was disconencted]
 * 
 * @author -Nemesiss-
 */
public class CM_ACCOUNT_DISCONNECTED extends GsClientPacket {

	/**
	 * AccountId of account that was disconnected form GameServer.
	 */
	private int accountId;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		accountId = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Account account = this.getConnection().getGameServerInfo().removeAccountFromGameServer(accountId);

		/**
		 * account can be null if a player logged out from gs {@link CM_ACCOUNT_RECONNECT_KEY 
		 */
		if (account != null) {
			AccountTimeController.updateOnLogout(account);
		}
	}
}
