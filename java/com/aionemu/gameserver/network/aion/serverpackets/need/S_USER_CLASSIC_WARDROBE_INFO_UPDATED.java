package com.aionemu.gameserver.network.aion.serverpackets.need;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_USER_CLASSIC_WARDROBE_INFO_UPDATED extends AionServerPacket {

    private final int slot;

    public S_USER_CLASSIC_WARDROBE_INFO_UPDATED(int slot) {
        this.slot = slot;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeH(this.slot);
    }
}