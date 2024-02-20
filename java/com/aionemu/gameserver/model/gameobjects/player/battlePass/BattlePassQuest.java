package com.aionemu.gameserver.model.gameobjects.player.battlePass;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.battle_pass.BattlePassActionTemplate;
import com.aionemu.gameserver.model.templates.battle_pass.BattlePassQuestTemplate;
import com.aionemu.gameserver.model.templates.battle_pass.BattleQuestType;

import java.sql.Timestamp;

public class BattlePassQuest {

    private int objectId;
    private int id;
    private BattleQuestType type;
    private BattleQuestState state;
    private int step;
    private Timestamp startDate;
    private Timestamp endateDate;
    private BattlePassQuestTemplate template;
    private BattlePassActionTemplate actionTemplate;
    private int seasonId;

    public BattlePassQuest(int objectId, int id, BattleQuestType type, BattleQuestState state, int step, Timestamp startDate, Timestamp endateDate) {
        this.objectId = objectId;
        this.id = id;
        this.type = type;
        this.state = state;
        this.step = step;
        this.startDate = startDate;
        this.endateDate = endateDate;
        this.template = DataManager.BATTLE_PASS_QUEST_DATA.getQuestById(this.id);
        this.actionTemplate = DataManager.BATTLE_PASS_ACTION_DATA.getActionById(this.template.getActionId());
        this.seasonId = this.template.getPassId();
    }

    public int getObjectId() {
        return objectId;
    }

    public int getId() {
        return id;
    }

    public BattleQuestType getType() {
        return type;
    }

    public BattleQuestState getState() {
        return state;
    }

    public void setState(BattleQuestState state) {
        this.state = state;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public Timestamp getEndateDate() {
        return endateDate;
    }

    public BattlePassActionTemplate getActionTemplate() {
        return actionTemplate;
    }

    public BattlePassQuestTemplate getTemplate() {
        return template;
    }

    public int getSeasonId() {
        return seasonId;
    }
}
