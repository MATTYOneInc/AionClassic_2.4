package com.aionemu.gameserver.network.aion.serverpackets.need;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_NPSHOP_GOODS_CHANGE extends AionServerPacket {

    private final int amount;

    public S_NPSHOP_GOODS_CHANGE(int amount) {
        this.amount = amount;

    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeD(this.amount);
    }
}
