package com.aionemu.gameserver.model.stats.calc.functions;

import com.aionemu.gameserver.model.stats.container.StatEnum;

class PvPAttackRatioFunction extends DuplicateStatFunction
{
	PvPAttackRatioFunction() {
		stat = StatEnum.PVP_ATTACK_RATIO;
	}
}