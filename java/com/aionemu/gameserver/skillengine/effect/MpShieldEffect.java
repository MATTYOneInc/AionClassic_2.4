package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.controllers.observer.AttackCalcObserver;
import com.aionemu.gameserver.controllers.observer.AttackShieldObserver;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.skillengine.model.Effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MpShieldEffect")
public class MpShieldEffect extends EffectTemplate
{
	@XmlAttribute
	protected int hitdelta;
	
	@XmlAttribute
	protected int hitvalue;
	
	@XmlAttribute
	protected boolean percent;
	
	@XmlAttribute
	protected int radius = 0;
	
	@XmlAttribute
	protected int minradius = 0;
	
	@XmlAttribute
	protected Race condrace = null;
	
	@Override
	public void applyEffect(Effect effect) {
		if (condrace != null && effect.getEffected().getRace() != condrace) {
			return;
		}
		effect.addToEffectedController();
	}
	
	@Override
	public void calculate(Effect effect) {
		effect.addSucessEffect(this);
	}
	
	@Override
	public void startEffect(final Effect effect) {
		int skillLvl = effect.getSkillLevel();
		int valueWithDelta = value + delta * skillLvl;
		int hitValueWithDelta = hitvalue + hitdelta * skillLvl;
		AttackShieldObserver asObserver = new AttackShieldObserver(hitValueWithDelta, valueWithDelta, percent, effect, hitType, this.getType(), this.hitTypeProb);
		effect.getEffected().getObserveController().addAttackCalcObserver(asObserver);
		effect.setAttackShieldObserver(asObserver, position);
		effect.getEffected().getEffectController().setUnderShield(true);
	}
	
	@Override
	public void endEffect(Effect effect) {
		AttackCalcObserver acObserver = effect.getAttackShieldObserver(position);
		if (acObserver != null) {
			effect.getEffected().getObserveController().removeAttackCalcObserver(acObserver);
		}
		effect.getEffected().getEffectController().setUnderShield(false);
	}
	
	public int getType() {
		return 2;
	}
}