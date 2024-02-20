package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * Created by wanke on 14/02/2017.
 */

public class S_NPSHOP_GOODS_COUNT extends AionServerPacket
{

    private final int count;

    public S_NPSHOP_GOODS_COUNT(int count) {
        this.count = count;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeD(this.count); //reward size
    }
}