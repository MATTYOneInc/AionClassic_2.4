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
 * This packet is used to update mp / max mp value.
 * 
 * @author Luno
 */
public class S_MANA_POINT extends AionServerPacket {

	private int currentMp;
	private int maxMp;

	/**
	 * @param currentMp
	 * @param maxMp
	 */
	public S_MANA_POINT(int currentMp, int maxMp) {
		this.currentMp = currentMp;
		this.maxMp = maxMp;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(currentMp);
		writeD(maxMp);
	}

}
