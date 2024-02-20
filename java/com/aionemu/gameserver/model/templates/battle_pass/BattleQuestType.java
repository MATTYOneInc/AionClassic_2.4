package com.aionemu.gameserver.model.templates.battle_pass;

public enum BattleQuestType {
    DAILY(1),
    WEEKLY(2),
    SEASON(3);

    private int value;

    BattleQuestType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static BattleQuestType getAchievementTypeByString(String fieldName) {
        for (BattleQuestType at: values()) {
            if (at.toString().equals(fieldName)) {
                return at;
            }
        }
        return null;
    }

    public static BattleQuestType getPlayerClassById(int classId) {
        for (BattleQuestType pc : values()) {
            if (pc.getValue() == classId) {
                return pc;
            }
        }
        throw new IllegalArgumentException("There is no player class with id " + classId);
    }
}
