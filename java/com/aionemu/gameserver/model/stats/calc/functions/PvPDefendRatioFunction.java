package com.aionemu.gameserver.model.stats.calc.functions;

import com.aionemu.gameserver.model.stats.container.StatEnum;

class PvPDefendRatioFunction extends DuplicateStatFunction
{
	PvPDefendRatioFunction() {
		stat = StatEnum.PVP_DEFEND_RATIO;
	}
}