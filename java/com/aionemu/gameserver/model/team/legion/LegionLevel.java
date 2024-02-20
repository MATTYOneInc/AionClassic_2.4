package com.aionemu.gameserver.model.team.legion;

import com.aionemu.gameserver.model.PlayerClass;

public enum LegionLevel {

    LEVEL1(1, 10000, 0, 0, 24, 24),
    LEVEL2(2, 100000, 15000, 10, 36, 32),
    LEVEL3(3, 1000000, 60000, 15, 48, 40),
    LEVEL4(4, 5000000, 240000, 20, 60, 48),
    LEVEL5(5, 25000000, 960000, 25, 72, 56);

    private int level;
    private int kinah;
    private int contribution;
    private int requireMember;
    private int maxMember;
    private int warehouseSlot;

    private LegionLevel(int level, int kinah, int contribution, int requireMember, int maxMember, int warehouseSlot) {
        this.level = level;
        this.kinah = kinah;
        this.contribution = contribution;
        this.requireMember = requireMember;
        this.maxMember = maxMember;
        this.warehouseSlot = warehouseSlot;
    }

    public static LegionLevel getLegionLevel(int level) {
        for (LegionLevel pc : values()) {
            if (pc.getLevel() == level)
                return pc;
        }
        throw new IllegalArgumentException("There is no legion level with id " + level);
    }

    public int getLevel() {
        return level;
    }

    public int getKinah() {
        return kinah;
    }

    public int getContribution() {
        return contribution;
    }

    public int getRequireMember() {
        return requireMember;
    }

    public int getMaxMember() {
        return maxMember;
    }

    public int getWarehouseSlot() {
        return warehouseSlot;
    }
}
