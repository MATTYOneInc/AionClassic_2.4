package com.aionemu.gameserver.model.gameobjects.player.battlePass;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.battle_pass.BattlePassRewardTemplate;

public class BattlePassReward {

    private final int id;
    private boolean rewarded;
    private boolean unlockReward;
    private BattlePassRewardTemplate template;

    public BattlePassReward(int id, boolean rewarded, boolean unlockReward) {
        this.id = id;
        this.rewarded = rewarded;
        this.unlockReward = unlockReward;
        this.template = DataManager.BATTLE_PASS_REWARD_DATA.getRewardById(this.id);
    }

    public int getId() {
        return id;
    }

    public BattlePassRewardTemplate getTemplate() {
        return template;
    }

    public boolean isUnlockReward() {
        return unlockReward;
    }

    public void setUnlockReward(boolean unlockReward) {
        this.unlockReward = unlockReward;
    }

    public boolean isRewarded() {
        return rewarded;
    }

    public void setRewarded(boolean rewarded) {
        this.rewarded = rewarded;
    }
}
