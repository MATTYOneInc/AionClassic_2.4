package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_SPAM_FILTER_FLAG extends AionServerPacket {

    private final Player player;

    public S_SPAM_FILTER_FLAG(Player player) {
        this.player = player;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeD(this.player.getObjectId());
        writeD(0);
    }
}
