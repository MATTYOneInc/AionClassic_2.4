package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_DELETE_GUILD_JOIN_APPLICANT extends AionServerPacket {

    private int requesterId;
    private boolean allowed;

    public S_DELETE_GUILD_JOIN_APPLICANT(int requesterId, boolean allowed) {
        this.requesterId = requesterId;
        this.allowed = allowed;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeD(requesterId);
        writeC(allowed ? 1 : 2);
    }
}