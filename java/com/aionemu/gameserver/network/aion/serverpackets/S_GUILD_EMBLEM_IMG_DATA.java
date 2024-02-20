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
 * @author cura
 */
public class S_GUILD_EMBLEM_IMG_DATA extends AionServerPacket {

	private int size;
	private byte[] data;

	public S_GUILD_EMBLEM_IMG_DATA(int size, byte[] data) {
		this.size = size;
		this.data = data;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(size);
		writeB(data);
	}
}
