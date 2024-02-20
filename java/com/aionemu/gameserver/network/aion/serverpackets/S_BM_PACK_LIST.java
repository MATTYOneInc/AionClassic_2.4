package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_BM_PACK_LIST extends AionServerPacket
{
    @Override
    protected void writeImpl(AionConnection con) {
        writeH(0);
    }
}
