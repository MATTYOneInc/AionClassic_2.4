package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.world.WorldPosition;

public class S_CHANGE_CHANNEL extends AionServerPacket
{
	int instanceCount = 0;
	int currentChannel = 0;
	
	public S_CHANGE_CHANNEL(WorldPosition position) {
		this.instanceCount = position.getInstanceCount();
		this.currentChannel = position.getInstanceId() - 1;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(currentChannel);
		writeD(instanceCount);
	}
}