package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_GROUP_INFO extends AionServerPacket
{
	
	@Override
    protected void writeImpl(AionConnection con) {
        Player player = con.getActivePlayer();
        this.writeD(0);
        this.writeC(0);
        this.writeD(255);
        this.writeD(0);
        this.writeH(0);
    }
}