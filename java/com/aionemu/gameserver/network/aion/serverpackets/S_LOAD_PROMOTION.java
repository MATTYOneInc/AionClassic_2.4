package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerEventWindow;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_LOAD_PROMOTION extends AionServerPacket {

    private final Player player;

    public S_LOAD_PROMOTION(Player player) {
        this.player = player;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeC(1); //active
        writeH(this.player.getPlayerEventWindow().size());
        for (PlayerEventWindow eventWindow : this.player.getPlayerEventWindow().values()) {
            int displayTime = (eventWindow.getTemplate().getRemainingTime());
            writeD(eventWindow.getId());
            writeD(eventWindow.getElapsed() * 60);
            writeD(displayTime * 60);
            writeD(0);
            writeD(0);// This is Max Count of Day
            writeD((int) (System.currentTimeMillis() / 1000)); // PlayerLoginTime
            writeC(1); //maybe pc
            writeD(7);
            writeD(1);
            writeD(0);
            writeD(eventWindow.getTemplate().getRemainingTime());
            writeD(eventWindow.getTemplate().getItemId());
            writeQ(eventWindow.getTemplate().getCount());
            writeD(132);
            writeQ(eventWindow.getTemplate().getPeriodStart().getMillis() / 1000); // Period Start TimeStamp
            writeQ(eventWindow.getTemplate().getPeriodEnd().getMillis() / 1000); // Period Start TimeStamp
            writeB(new byte[12]);
            writeD(eventWindow.getTemplate().getMinLevel());
            writeD(eventWindow.getTemplate().getMaxLevel());
            writeD(-1);
            writeB(new byte[84]);
            writeD(-1);
            writeB(new byte[8]);// Do not Change !!!
            writeD(1);
            writeD(9);
            writeD(eventWindow.getTemplate().getMaxCountOfDay() - eventWindow.getReceivedCount()); //max count
            writeB(new byte[7]);// Do not Change !!!
            writeD(-1);
            writeD(906295); //name id dont tuch
        }
    }
}
