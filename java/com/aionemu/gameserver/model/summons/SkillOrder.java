package com.aionemu.gameserver.model.summons;

import com.aionemu.gameserver.model.gameobjects.Creature;

public class SkillOrder
{
    private final int skillId;
    private final int skillLvl;
    private final Creature target;
    private final boolean release;
	
    public SkillOrder(int skillId, int skillLvl, boolean release, Creature target) {
        this.skillId = skillId;
        this.target = target;
        this.release = release;
        this.skillLvl = skillLvl;
    }
	
    public int getSkillId() {
        return this.skillId;
    }
	
    public Creature getTarget() {
        return this.target;
    }
	
    public boolean isRelease() {
        return this.release;
    }
	
    public int getSkillLevel() {
        return this.skillLvl;
    }
}