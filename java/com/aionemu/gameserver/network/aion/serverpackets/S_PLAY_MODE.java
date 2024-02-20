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
 * ascension quest's morph
 * 
 * @author wylovech
 */
public class S_PLAY_MODE extends AionServerPacket {

	private int inascension;

	public S_PLAY_MODE(int inascension) {
		this.inascension = inascension;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeC(inascension);// if inascension =0x01 morph.
		writeC(0x00); // new 2.0 Packet --- probably pet info?
	}
}
