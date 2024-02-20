package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.skillengine.model.Effect;

public class SanctuaryEffect extends EffectTemplate {

	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}

	public void startEffect(Effect effect) {
	}

	public void endEffect(Effect effect) {
	}
}
