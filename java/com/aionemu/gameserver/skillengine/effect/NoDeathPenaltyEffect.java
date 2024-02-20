package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.skillengine.model.Effect;

public class NoDeathPenaltyEffect extends BuffEffect {

	public void calculate(Effect effect) {
		effect.addSucessEffect(this);
	}

	public void startEffect(Effect effect) {
		effect.setNoDeathPenalty(true);
	}
}
