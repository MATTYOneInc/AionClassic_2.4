package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_INVINCIBLE_TIME extends AionServerPacket {

    private int time;

    public S_INVINCIBLE_TIME(int time){
        this.time = time;
    }

    protected void writeImpl(AionConnection con){
        writeD(time);
    }

}
