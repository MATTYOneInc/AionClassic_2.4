package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_REQUEST_TELEPORT extends AionServerPacket
{
    private int portAnimation;
    private int mapId;
    private int instanceId;
    private float x;
    private float y;
    private float z;
    private byte heading;
    private boolean isInstance;
	
    public S_REQUEST_TELEPORT(boolean isInstance, int instanceId, int mapId, float x, float y, float z, byte heading, int portAnimation) {
        this.isInstance = isInstance;
        this.instanceId = instanceId;
		this.mapId = mapId;
		this.x = x;
		this.y = y;
		this.z = z;
		this.heading = heading;
        this.portAnimation = portAnimation;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeC(portAnimation);
		writeD(mapId);
		writeD(isInstance ? instanceId : mapId);
		writeF(x);
		writeF(y);
		writeF(z);
		writeC(heading);
    }
}