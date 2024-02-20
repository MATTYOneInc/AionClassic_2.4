package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_GAMEPASS_INFO extends AionServerPacket {

    private final int id;
    private final Player player;

    public S_GAMEPASS_INFO(int id, Player player) {
        this.id = id;
        this.player = player;
    }


    @Override
    protected void writeImpl(AionConnection con) {
        writeD(this.id);
        switch (this.id) {
            case 1:
                writeQ(-System.currentTimeMillis() / 1000);
                writeQ(-System.currentTimeMillis() / 1000);
                break;
            case 2:
                writeQ(-this.player.getAccountSielEnergy().getStart().getTime() / 1000);
                writeQ(this.player.getAccountSielEnergy().getTime());
                break;
            case 3:
                writeQ(this.player.getPlayerAccount().getMembershipExpire().getTime() /1000 - (System.currentTimeMillis() / 1000)); //seconds
                writeQ(0);
                writeQ(0);
        }
    }
}
