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
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.container.StatEnum;

public class ReverseStat extends Stat2
{
    public ReverseStat(StatEnum stat, int base, Creature owner) {
        super(stat, base, owner);
    }

    public ReverseStat(StatEnum stat, int base, Creature owner, float bonusRate) {
        super(stat, base, owner, bonusRate);
    }

    @Override
    public void addToBase(int base) {
        this.base -= base;
        if (this.base < 0) {
            this.base = 0;
        }
    }

    @Override
    public void addToBonus(int bonus) {
        this.bonus = (int)((float)this.bonus - this.bonusRate * (float)bonus);
    }

    @Override
    public float calculatePercent(int delta) {
        float percent = (float)(100 - delta) / 100.0f;
        return percent < 0.0f ? 0.0f : percent;
    }
}