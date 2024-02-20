package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolymorphEffect")
public class PolymorphEffect extends TransformEffect
{
	@Override
	public void startEffect(Effect effect) {
		if ((effect.getEffector() instanceof Player)) {
			if (effect.getEffector().getEffectController().isAbnormalSet(AbnormalState.HIDE)) {
				effect.getEffector().getEffectController().removeHideEffects();
			}
		}
		super.startEffect(effect);
	}
	
	@Override
	public void endEffect(Effect effect) {
		super.endEffect(effect);
		effect.getEffected().getTransformModel().setActive(false);
	}
}