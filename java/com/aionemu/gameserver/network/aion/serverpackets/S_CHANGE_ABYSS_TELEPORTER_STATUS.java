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

public class S_CHANGE_ABYSS_TELEPORTER_STATUS extends AionServerPacket
{
	private int locationId;
	private boolean teleportStatus;

	public S_CHANGE_ABYSS_TELEPORTER_STATUS(int locationId, boolean teleportStatus) {
		this.locationId = locationId;
		this.teleportStatus = teleportStatus;
	}

	protected void writeImpl(AionConnection con) {
		writeD(locationId);
		writeC(teleportStatus ? 1 : 0);
	}
}