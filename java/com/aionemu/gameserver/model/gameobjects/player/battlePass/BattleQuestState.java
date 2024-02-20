package com.aionemu.gameserver.model.gameobjects.player.battlePass;

public enum BattleQuestState {

    START(1),
    REWARD(2),
    COMPLETE(3);

    private int value;

    BattleQuestState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static BattleQuestState getPlayerClassById(int classId) {
        for (BattleQuestState pc : values()) {
            if (pc.getValue() == classId) {
                return pc;
            }
        }
        throw new IllegalArgumentException("There is no BattleQuestState with id " + classId);
    }
}
