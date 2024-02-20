package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.skillengine.action.DamageType;
import com.aionemu.gameserver.skillengine.model.Effect;

public class DeathBlowEffect extends DamageEffect {

	public void calculate(Effect effect) {
		super.calculate(effect, DamageType.MAGICAL);
	}
}
