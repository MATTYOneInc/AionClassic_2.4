package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.controllers.observer.AttackCalcObserver;
import com.aionemu.gameserver.controllers.observer.AttackShieldObserver;
import com.aionemu.gameserver.skillengine.model.Effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReflectorEffect")
public class ReflectorEffect extends ShieldEffect
{
	@Override
	public void startEffect(final Effect effect) {
		int hit = hitvalue + hitdelta * effect.getSkillLevel();
		AttackShieldObserver asObserver = new AttackShieldObserver(hit, this.value, this.percent, false, effect, this.hitType, getType(), this.hitTypeProb, this.minradius, this.radius, null, 0, 0);
		effect.getEffected().getObserveController().addAttackCalcObserver(asObserver);
		effect.setAttackShieldObserver(asObserver, position);
		effect.getEffected().getEffectController().setUnderShield(true);
		if (hit >= 100) {
			effect.getEffected().getEffectController().setUnderholyShield(true);
		}
	}
	
	@Override
	public void endEffect(Effect effect) {
		AttackCalcObserver acObserver = effect.getAttackShieldObserver(position);
		if (acObserver != null) {
			effect.getEffected().getObserveController().removeAttackCalcObserver(acObserver);
			effect.getEffected().getEffectController().setUnderShield(false);
			effect.getEffected().getEffectController().setUnderholyShield(false);
		}
	}
	
	@Override
	public int getType() {
		return 1;
	}
}