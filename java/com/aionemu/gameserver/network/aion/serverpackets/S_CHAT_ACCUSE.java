package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_CHAT_ACCUSE extends AionServerPacket {

    private final Player player;

    public S_CHAT_ACCUSE(Player player) {
        this.player = player;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeD(this.player.getObjectId());
        writeC(0); //maybe accused
    }
}
