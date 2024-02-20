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
package com.aionemu.gameserver.network.aion.serverpackets;


import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Nemiroff
 */
public class S_FLIGHT_POINT extends AionServerPacket {

	private int currentFp;
	private int maxFp;

	public S_FLIGHT_POINT(int currentFp, int maxFp) {
		this.currentFp = currentFp;
		this.maxFp = maxFp;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(currentFp); // current fly time
		writeD(maxFp); // max flytime
	}
}
