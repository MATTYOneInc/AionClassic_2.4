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
public class SM_PLAY_OK extends AionServerPacket {

	/**
	 * playOk1 is part of session key - its used for security purposes [checked at game server side]
	 */
	private final int playOk1;
	/**
	 * playOk2 is part of session key - its used for security purposes [checked at game server side]
	 */
	private final int playOk2;
	private int serverId;
	/**
	 * Constructs new instance of <tt>SM_PLAY_OK </tt> packet.
	 *
	 * @param key
	 *          session key
	 */
	public SM_PLAY_OK(SessionKey key, byte serverId)
	{
		super(0x07);
		this.playOk1 = key.playOk1;
		this.playOk2 = key.playOk2;
		this.serverId = serverId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(LoginConnection con)
	{
		writeC(getOpcode());
		writeD(playOk1);
		writeD(playOk2);
		writeC(serverId);
		writeB(new byte[14]);
	}
}
