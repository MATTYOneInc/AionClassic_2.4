package com.aionemu.gameserver.model.gameobjects.player.battlePass;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.battle_pass.BattlePassSeasonTemplate;
import javolution.util.FastMap;

import java.util.Map;

public class BattlePassSeason {

    private int id;
    private int level;
    private long exp;
    private BattlePassSeasonTemplate template;
    Map<Integer, BattlePassReward> rewards = new FastMap<Integer, BattlePassReward>();

    public BattlePassSeason(int id, int level, long exp) {
        this.id = id;
        this.level = level;
        this.exp = exp;
        this.template = DataManager.BATTLE_PASS_DATA.getSeasonById(this.id);
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public long getExp() {
        return exp;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public void addExp(long exp) {
        this.exp += exp;
    }

    public BattlePassSeasonTemplate getTemplate() {
        return template;
    }

    public Map<Integer, BattlePassReward> getRewards() {
        return rewards;
    }

    public void setRewards(Map<Integer, BattlePassReward> rewards) {
        this.rewards = rewards;
    }
}
