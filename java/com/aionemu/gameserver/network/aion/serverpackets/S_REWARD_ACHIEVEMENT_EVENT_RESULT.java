package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.battlePass.BattlePassQuest;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_REWARD_ACHIEVEMENT_EVENT_RESULT extends AionServerPacket {

    private final BattlePassQuest battlePassQuest;

    public S_REWARD_ACHIEVEMENT_EVENT_RESULT(BattlePassQuest battlePassQuest) {
        this.battlePassQuest = battlePassQuest;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeC(1); //dont tuch
        writeD(this.battlePassQuest.getId());
        writeQ(this.battlePassQuest.getObjectId());
    }
}
