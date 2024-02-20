package com.aionemu.gameserver.model.stats.calc.functions;

import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.stats.container.StatEnum;

class MaxMpFunction extends StatFunction {

	MaxMpFunction() {
		stat = StatEnum.MAXMP;
	}

	@Override
	public void apply(Stat2 stat) {
		float will = stat.getOwner().getGameStats().getWill().getCurrent();
		stat.setBaseRate(will * 0.01f);
	}

	@Override
	public int getPriority() {
		return 30;
	}
}