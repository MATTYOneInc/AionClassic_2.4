package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillType;
import com.aionemu.gameserver.skillengine.model.SpellStatus;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SilenceEffect")
public class SilenceEffect extends EffectTemplate
{
	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}
	
	@Override
	public void calculate(Effect effect) {
		super.calculate(effect, StatEnum.SILENCE_RESISTANCE, SpellStatus.SILENCE);
	}
	
	@Override
	public void startEffect(Effect effect) {
		final Creature effected = effect.getEffected();
		effect.setAbnormal(AbnormalState.SILENCE.getId());
		effected.getEffectController().setAbnormal(AbnormalState.SILENCE.getId());
		if (effected.getCastingSkill() != null && effected.getCastingSkill().getSkillTemplate().getType() == SkillType.MAGICAL) {
			effected.getController().cancelCurrentSkill();
		}
	}
	
	@Override
	public void endEffect(Effect effect) {
		super.endEffect(effect);
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.SILENCE.getId());
	}
}