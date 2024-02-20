package com.aionemu.gameserver.model.stats.calc.functions;

import com.aionemu.gameserver.model.stats.container.StatEnum;

class HealBoostFunction extends DuplicateStatFunction
{
	HealBoostFunction() {
		stat = StatEnum.HEAL_BOOST;
	}
}