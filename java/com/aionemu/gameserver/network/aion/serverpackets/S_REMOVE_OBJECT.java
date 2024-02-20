package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_REMOVE_OBJECT extends AionServerPacket
{
	private final int objectId;
	private final int time;
	
	public S_REMOVE_OBJECT(AionObject object, int time) {
		this.objectId = object.getObjectId();
		this.time = time;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		int action = 0;
		if (action != 1) {
			writeD(objectId);
			writeC(time);
		}
	}
}