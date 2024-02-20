package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.skillengine.model.Effect;

public class HiPassEffect extends BuffEffect {

	public void calculate(Effect effect) {
		effect.addSucessEffect(this);
	}

	public void startEffect(Effect effect) {
		effect.setHiPass(true);
	}
}
