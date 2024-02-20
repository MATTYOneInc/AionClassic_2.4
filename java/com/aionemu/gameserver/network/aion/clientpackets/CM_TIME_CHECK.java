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
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_SYNC_TIME;

/**
 * I dont know what this packet is doing - probably its ping/pong packet
 * 
 * @author -Nemesiss-
 */
public class CM_TIME_CHECK extends AionClientPacket {

	/**
	 * Nano time / 1000000
	 */
	private int nanoTime;

	/**
	 * Constructs new instance of <tt>CM_VERSION_CHECK </tt> packet
	 * 
	 * @param opcode
	 */
	public CM_TIME_CHECK(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		nanoTime = readD();
		readC();
		readD();
		readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		AionConnection client = getConnection();
		int timeNow = (int) (System.nanoTime() / 1000000);
		@SuppressWarnings("unused")
		int diff = timeNow - nanoTime;
		//client.sendPacket(new S_SYNC_TIME(nanoTime));

		// log.info("CM_TIME_CHECK: " + nanoTime + " =?= " + timeNow + " dif: " + diff);
	}
}
