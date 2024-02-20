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

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.loginserver.LoginServer;

/**
 * In this packets aion client is authenticating himself by providing accountId and rest of sessionKey - we will check
 * if its valid at login server side.
 * 
 * @author -Nemesiss-
 */
// TODO: L2AUTH? Really? :O
public class CM_L2AUTH_LOGIN_CHECK extends AionClientPacket {

	/**
	 * playOk2 is part of session key - its used for security purposes we will check if this is the key what login server
	 * sends.
	 */
	private int playOk2;
	/**
	 * playOk1 is part of session key - its used for security purposes we will check if this is the key what login server
	 * sends.
	 */
	private int playOk1;
	/**
	 * accountId is part of session key - its used for authentication we will check if this accountId is matching any
	 * waiting account login server side and check if rest of session key is ok.
	 */
	private int accountId;
	/**
	 * loginOk is part of session key - its used for security purposes we will check if this is the key what login server
	 * sends.
	 */
	private int loginOk;

	/**
	 * Constructs new instance of <tt>CM_L2AUTH_LOGIN_CHECK </tt> packet
	 * 
	 * @param opcode
	 */
	public CM_L2AUTH_LOGIN_CHECK(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		playOk2 = readD();
		playOk1 = readD();
		accountId = readD();
		loginOk = readD();
		readD();
		readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		LoginServer.getInstance().requestAuthenticationOfClient(accountId, getConnection(), loginOk, playOk1, playOk2);
	}
}
