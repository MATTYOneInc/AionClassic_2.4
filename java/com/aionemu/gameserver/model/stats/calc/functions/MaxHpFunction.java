package com.aionemu.gameserver.model.stats.calc.functions;

import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.container.StatEnum;

class MaxHpFunction extends StatFunction {

	MaxHpFunction() {
		stat = StatEnum.MAXHP;
	}

	@Override
	public void apply(Stat2 stat) {
		float health = stat.getOwner().getGameStats().getHealth().getCurrent();
		stat.setBaseRate(health * 0.01f);
	}

	@Override
	public int getPriority() {
		return 30;
	}
}