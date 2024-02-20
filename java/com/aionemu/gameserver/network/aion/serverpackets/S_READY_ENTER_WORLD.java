package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_READY_ENTER_WORLD extends AionServerPacket {

    private final int type;
    private final int code;

    public S_READY_ENTER_WORLD(int type, int code) {
        this.type = type;
        this.code = code;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeH(this.type);
        writeD(this.code);
    }
}
