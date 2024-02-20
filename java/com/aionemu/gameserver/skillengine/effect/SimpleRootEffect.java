package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SpellStatus;
import com.aionemu.gameserver.skillengine.model.SkillMoveType;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SimpleRootEffect")
public class SimpleRootEffect extends EffectTemplate
{
	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}
	
	@Override
	public void calculate(Effect effect) {
		if (effect.getEffected().getEffectController().hasAbnormalEffect(1968) ||
		    effect.getEffected().getEffectController().hasAbnormalEffect(2538) ||
		    effect.getEffected().getEffectController().hasAbnormalEffect(8224) ||
		    effect.getEffected().getEffectController().hasAbnormalEffect(8678)) {
			return;
        } if (super.calculate(effect, StatEnum.STAGGER_RESISTANCE, SpellStatus.STAGGER)) {
			return;
		}
	}
	
	@Override
	public void startEffect(final Effect effect) {
		final Creature effected = effect.getEffected();
		effected.getMoveController().abortMove();
		effected.getController().cancelCurrentSkill();
		effect.setSkillMoveType(SkillMoveType.KNOCKBACK);
		effect.getEffected().getEffectController().setAbnormal(AbnormalState.KNOCKBACK.getId());
		effect.setAbnormal(AbnormalState.KNOCKBACK.getId());
	}
	
	@Override
	public void endEffect(Effect effect) {
		super.endEffect(effect);
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.KNOCKBACK.getId());
	}
}