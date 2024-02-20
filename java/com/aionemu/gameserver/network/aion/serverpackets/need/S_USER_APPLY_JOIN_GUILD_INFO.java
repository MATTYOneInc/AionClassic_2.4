package com.aionemu.gameserver.network.aion.serverpackets.need;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_USER_APPLY_JOIN_GUILD_INFO extends AionServerPacket {

    private int legionId;
    private String legionName;

    public S_USER_APPLY_JOIN_GUILD_INFO(int legionId, String legionName) {
        this.legionId = legionId;
        this.legionName = legionName;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeD(legionId);
        writeS(legionName);
    }
}