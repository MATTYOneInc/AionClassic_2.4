package com.aionemu.gameserver.network.aion.serverpackets.missing;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_INSTANCE_COUNT_INFO extends AionServerPacket {

	private int mapId;
	private int instanceId;

	public SM_INSTANCE_COUNT_INFO(int mapId, int instanceId) {
		this.mapId = mapId;
		this.instanceId = instanceId;
	}

	protected void writeImpl(AionConnection con) {
		writeD(mapId);
		writeD(instanceId);
		writeD(1);
	}
}
