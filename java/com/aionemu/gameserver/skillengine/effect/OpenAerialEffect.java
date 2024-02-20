package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SpellStatus;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OpenAerialEffect")
public class OpenAerialEffect extends EffectTemplate
{
	@Override
    public void applyEffect(Effect effect) {
        if (!effect.getEffected().getEffectController().hasPhysicalStateEffect() &&
		    !effect.getEffected().getEffectController().isAbnormalSet(AbnormalState.STUMBLE) &&
		    !effect.getEffected().getEffectController().isAbnormalSet(AbnormalState.STUN)) {
            effect.addToEffectedController();
			effect.setIsPhysicalState(true);
            effect.getEffected().getEffectController().removeParalyzeEffects();
        }
    }
	
	@Override
	public void calculate(Effect effect) {
		super.calculate(effect, StatEnum.OPENAREIAL_RESISTANCE, SpellStatus.OPENAERIAL);
	}
	
	@Override
	public void startEffect(Effect effect) {
		final Creature effected = effect.getEffected();
		effected.getController().cancelCurrentSkill();
		effected.getMoveController().abortMove();
		effected.getEffectController().setAbnormal(AbnormalState.OPENAERIAL.getId());
		effect.setAbnormal(AbnormalState.OPENAERIAL.getId());
	}
	
	@Override
	public void endEffect(Effect effect) {
		effect.setIsPhysicalState(false);
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.OPENAERIAL.getId());
	}
}