package com.aionemu.gameserver.network.aion.serverpackets.need;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_LOAD_CHANNEL_CHATTING_BLACKLIST extends AionServerPacket {

    @Override
    protected void writeImpl(AionConnection con) {
        writeH(0); //size temp to 0 dont need this features
    }
}