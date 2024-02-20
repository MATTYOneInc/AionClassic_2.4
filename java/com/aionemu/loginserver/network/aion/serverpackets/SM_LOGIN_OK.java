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
package com.aionemu.loginserver.network.aion.serverpackets;

import com.aionemu.loginserver.network.aion.AionServerPacket;
import com.aionemu.loginserver.network.aion.LoginConnection;
import com.aionemu.loginserver.network.aion.SessionKey;

/**
 * @author -Nemesiss-
 */
public class SM_LOGIN_OK extends AionServerPacket {

	/**
	 * accountId is part of session key - its used for security purposes
	 */
	private final int accountId;
	/**
	 * loginOk is part of session key - its used for security purposes
	 */
	private final int loginOk;

	/**
	 * Constructs new instance of <tt>SM_LOGIN_OK</tt> packet.
	 * 
	 * @param key
	 *          session key
	 */
	public SM_LOGIN_OK(SessionKey key) {
		super(0x03);
		this.accountId = key.accountId;
		this.loginOk = key.loginOk;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(LoginConnection con) {
		writeC(getOpcode());
		writeD(accountId);
		writeD(loginOk);
		writeD(0x00);
		writeD(0x00);
		writeD(1002);
		writeB("E4C85FEB000000000000000000000000000000000000000000000000000000004E5464454D6A52424F6A59ACC02BB3314F446C");
	}
}
