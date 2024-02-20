package com.aionemu.gameserver.model.stats.calc.functions;

import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.stats.container.StatEnum;

class AgilityModifierFunction extends StatFunction
{
	private float modifier;

    AgilityModifierFunction(StatEnum stat, float modifier) {
        this.stat = stat;
        this.modifier = modifier;
    }

    @Override
    public void apply(Stat2 stat) {
        float agility = stat.getOwner().getGameStats().getAgility().getCurrent();
        stat.setBase(Math.round((float)stat.getBase() + (float)stat.getBase() * (agility - 100.0f) * this.modifier / 100.0f));
    }

    @Override
    public int getPriority() {
        return 30;
    }
}