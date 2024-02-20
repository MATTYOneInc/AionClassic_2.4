package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.battlePass.BattlePassReward;
import com.aionemu.gameserver.model.gameobjects.player.battlePass.BattlePassSeason;
import com.aionemu.gameserver.model.gameobjects.player.battlePass.PlayerBattlePass;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_BATTLEPASS_LIST extends AionServerPacket {

    private final Player player;
    private final PlayerBattlePass pass;

    public S_BATTLEPASS_LIST(Player player) {
        this.player = player;
        this.pass = player.getPlayerBattlePass();
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeH(this.pass.getBattlePassSeason().size());
        for(BattlePassSeason season : this.pass.getBattlePassSeason().values()){
            writeD(season.getId());
            writeD(1);
            writeD(1);
            writeD(season.getLevel());
            writeQ(season.getExp());
            writeQ(System.currentTimeMillis() / 1000); //date
            for (BattlePassReward reward : season.getRewards().values()) {
                writeC(reward.isRewarded() ? 1 : 0);
                writeC(reward.isUnlockReward() ? 1 : 0);
            }
            int bytes = 200 - (season.getTemplate().getMaxLevel() * 2);
            writeB(new byte[bytes]);
        }
    }
}
