package com.aionemu.gameserver.network.aion.serverpackets.need;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_RESPONSE_NPSHOP_GOODS_RECV extends AionServerPacket {

    private final long ObjectId;

    public S_RESPONSE_NPSHOP_GOODS_RECV(long objectId) {
        ObjectId = objectId;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeQ(this.ObjectId);
        writeD(0);
    }
}
