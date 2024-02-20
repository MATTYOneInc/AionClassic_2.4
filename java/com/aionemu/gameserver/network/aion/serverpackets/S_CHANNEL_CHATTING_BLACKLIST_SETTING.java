package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_CHANNEL_CHATTING_BLACKLIST_SETTING extends AionServerPacket {

    @Override
    protected void writeImpl(AionConnection con) {
        writeC(0);
    }
}
