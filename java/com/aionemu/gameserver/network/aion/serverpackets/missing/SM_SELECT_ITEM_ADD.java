package com.aionemu.gameserver.network.aion.serverpackets.missing;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_SELECT_ITEM_ADD extends AionServerPacket
{
	private int uniqueItemId;
    private int index;
    
    public SM_SELECT_ITEM_ADD(int uniqueItemId, int index) {
        this.uniqueItemId = uniqueItemId;
        this.index = index;
    }
    
    @Override
    protected void writeImpl(AionConnection con) {
        writeD(uniqueItemId);
        writeD(0x00);
        writeC(index);
    }
}