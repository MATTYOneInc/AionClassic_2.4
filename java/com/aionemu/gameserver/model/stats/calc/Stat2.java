/*
 *  Aion Classic Emu based on Aion Encom Source Files
 *
 *  ENCOM Team based on Aion-Lighting Open Source
 *  All Copyrights : "Data/Copyrights/AEmu-Copyrights.text
 *
 *  iMPERIVM.FUN - AION DEVELOPMENT FORUM
 *  Forum: <http://https://imperivm.fun/>
 *
 */
package com.aionemu.gameserver.model.stats.calc;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.stats.container.StatEnum;

public abstract class Stat2
{
    float bonusRate = 1.0f;
    float baseRate = 1.0f;
    int base;
    int bonus;
    private final Creature owner;
    protected final StatEnum stat;

    public Stat2(StatEnum stat, int base, Creature owner) {
        this(stat, base, owner, 1.0f);
    }

    public Stat2(StatEnum stat, int base, Creature owner, float bonusRate) {
        this.stat = stat;
        this.base = base;
        this.owner = owner;
        this.bonusRate = bonusRate;
    }

    public final StatEnum getStat() {
        return this.stat;
    }

    public final int getBase() {
        return (int)((float)this.base * this.getBaseRate());
    }

    public final void setBase(int base) {
        this.base = base;
    }

    public final float getBaseRate() {
        return this.baseRate;
    }

    public final void setBaseRate(float rate) {
        this.baseRate = rate;
    }

    public abstract void addToBase(int base);

    public final int getBonus() {
        return this.bonus;
    }

    public final int getCurrent() {
        return (int)((float)this.base * this.baseRate + (float)this.bonus);
    }

    public final void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public final float getBonusRate() {
        return this.bonusRate;
    }

    public final void setBonusRate(float bonusRate) {
        this.bonusRate = bonusRate;
    }

    public abstract void addToBonus(int bonus);

    public abstract float calculatePercent(int delta);

    public final Creature getOwner() {
        return this.owner;
    }

    public String toString() {
        return "[" + this.stat.name() + " base=" + this.base + ", bonus=" + this.bonus + "]";
    }
}