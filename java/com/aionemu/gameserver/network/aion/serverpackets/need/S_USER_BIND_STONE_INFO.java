package com.aionemu.gameserver.network.aion.serverpackets.need;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_USER_BIND_STONE_INFO extends AionServerPacket {

    private final Player player;

    public S_USER_BIND_STONE_INFO(Player player) {
        this.player = player;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeD(player.getObjectId());
        writeD(0);
    }
}