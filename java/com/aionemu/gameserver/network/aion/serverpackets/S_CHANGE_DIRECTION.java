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
 * @author Nemesiss
 *
 */
public class S_CHANGE_DIRECTION extends AionServerPacket {
	private final int objectId;
	private final byte heading;

	public S_CHANGE_DIRECTION(int objectId, byte heading) {
		this.objectId = objectId;
		this.heading = heading;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(objectId);
		writeC(heading);
	}
}
