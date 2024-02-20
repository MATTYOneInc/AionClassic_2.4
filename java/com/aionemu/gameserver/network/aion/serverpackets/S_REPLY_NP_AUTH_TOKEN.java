package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_REPLY_NP_AUTH_TOKEN extends AionServerPacket
{
    private String bytes;

    public S_REPLY_NP_AUTH_TOKEN(String bytes) {
        this.bytes = bytes;
    }

    protected void writeImpl(AionConnection con) {
        writeB(this.bytes);
    }
}