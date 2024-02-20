package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.battlePass.BattlePassQuest;
import com.aionemu.gameserver.model.gameobjects.player.battlePass.PlayerBattlePass;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_CREATE_ACHIEVEMENT_EVENT extends AionServerPacket {

    private final Player player;

    public S_CREATE_ACHIEVEMENT_EVENT(Player player) {
        this.player = player;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeH(this.player.getPlayerBattlePass().getBattlePassQuest().size()); //size
        for (BattlePassQuest quest : this.player.getPlayerBattlePass().getBattlePassQuest().values()) {
            writeC(1); //active dont touch
            writeQ(quest.getObjectId()); //objectId
            writeD(quest.getId()); //id
            writeD(0);
            writeD(0);
            writeC(6); //type
            writeC(0);
            writeC(quest.getState().getValue()); //state
            writeD(quest.getStep()); //step
            writeC(0);
            writeD(0);
            writeD(0);
            writeD(0);
            writeD(0);
            writeD(0);
            writeQ(quest.getTemplate().getStartDate().getMillisecond()); //start
            writeQ(quest.getTemplate().getStartDate().getMillisecond()); //end
        }
    }
}
