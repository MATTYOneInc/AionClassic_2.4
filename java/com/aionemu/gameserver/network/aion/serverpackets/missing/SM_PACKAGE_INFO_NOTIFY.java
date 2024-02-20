package com.aionemu.gameserver.network.aion.serverpackets.missing;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_PACKAGE_INFO_NOTIFY extends AionServerPacket
{
	private int count;
    private int packId;
    private int time;
	
	public SM_PACKAGE_INFO_NOTIFY(int count, int packId, int time) {
        this.count = count;
        this.packId = packId;
        this.time = time;
    }
	
	@Override
    protected void writeImpl(AionConnection con) {
        Player activePlayer = con.getActivePlayer();
        writeH(count);
        writeC(packId);
        writeD(time);
    }
}