package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_CHANGE_ABYSS_PVP_STATUS extends AionServerPacket {

	private int locationId;
	private int state;

	public S_CHANGE_ABYSS_PVP_STATUS(SiegeLocation location) {
		locationId = location.getLocationId();
		state = (location.isVulnerable() ? 1 : 0);
	}

	public S_CHANGE_ABYSS_PVP_STATUS(int locationId, int state) {
		this.locationId = locationId;
		this.state = state;
	}

	protected void writeImpl(AionConnection con) {
		writeD(locationId);
		writeC(state);
	}
}
