package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.battlePass.BattlePassQuest;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

import java.util.List;

public class S_PROGRESS_ACHIEVEMENT extends AionServerPacket {

    private final List<BattlePassQuest> battlePassQuests;

    public S_PROGRESS_ACHIEVEMENT(List<BattlePassQuest> battlePassQuests) {
        this.battlePassQuests = battlePassQuests;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeH(this.battlePassQuests.size());
        for (BattlePassQuest quest : this.battlePassQuests) {
            writeQ(quest.getObjectId());
            writeC(quest.getState().getValue());
            writeD(quest.getStep());
            writeD(0);
            writeD(0);
            writeD(0);
            writeD(0);
            writeD(0);
        }
    }
}
