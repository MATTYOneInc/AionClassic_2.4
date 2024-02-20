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
package com.aionemu.loginserver.network.aion.clientpackets;

import java.nio.ByteBuffer;

import com.aionemu.loginserver.controller.AccountController;
import com.aionemu.loginserver.network.aion.AionClientPacket;
import com.aionemu.loginserver.network.aion.LoginConnection;

/**
 * This packet is send when client was connected to game server and now is reconnection to login server.
 * 
 * @author -Nemesiss-
 */
public class CM_UPDATE_SESSION extends AionClientPacket {

	/**
	 * accountId is part of session key - its used for security purposes
	 */
	private int accountId;
	/**
	 * loginOk is part of session key - its used for security purposes
	 */
	private int loginOk;
	/**
	 * reconectKey is key that server sends to client for fast reconnection to login server - we will check if this key is
	 * valid.
	 */
	private int reconnectKey;

	/**
	 * Constructs new instance of <tt>CM_UPDATE_SESSION </tt> packet.
	 * 
	 * @param buf
	 *          packet data
	 * @param client
	 *          client
	 */
	public CM_UPDATE_SESSION(ByteBuffer buf, LoginConnection client) {
		super(buf, client, 0x08);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		accountId = readD();
		loginOk = readD();
		reconnectKey = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		AccountController.authReconnectingAccount(accountId, loginOk, reconnectKey, getConnection());
	}
}
