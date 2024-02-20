package com.aionemu.gameserver.model.gameobjects.player.battlePass;

import com.aionemu.gameserver.model.templates.battle_pass.BattleQuestType;
import javolution.util.FastMap;

import java.util.Map;

public class PlayerBattlePass {

    private Map<Integer, BattlePassSeason> battlePassSeason;
    private Map<Integer, BattlePassQuest> battlePassQuest;

    public PlayerBattlePass() {
        this.battlePassSeason = new FastMap<Integer, BattlePassSeason>();
        this.battlePassQuest = new FastMap<Integer, BattlePassQuest>();
    }

    public Map<Integer, BattlePassSeason> getBattlePassSeason() {
        return battlePassSeason;
    }

    public void setBattlePassSeason(Map<Integer, BattlePassSeason> battlePassSeason) {
        this.battlePassSeason = battlePassSeason;
    }

    public Map<Integer, BattlePassQuest> getBattlePassQuest() {
        return battlePassQuest;
    }

    public void setBattlePassQuest(Map<Integer, BattlePassQuest> battlePassQuest) {
        this.battlePassQuest = battlePassQuest;
    }

    public boolean haveQuest(int questId) {
        boolean have = false;
        for (BattlePassQuest battlePassQuest : this.battlePassQuest.values()){
            if (battlePassQuest.getId() == questId) {
                have = true;
            }
        }
        return have;
    }
}
