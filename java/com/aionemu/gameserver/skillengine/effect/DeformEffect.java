package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.skillengine.model.Effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeformEffect")
public class DeformEffect extends TransformEffect
{
	@Override
	public void calculate(Effect effect) {
		super.calculate(effect, StatEnum.DEFORM_RESISTANCE, null);
	}
	
	@Override
	public void startEffect(Effect effect) {
		super.startEffect(effect);
	}

	@Override
	public void endEffect(Effect effect) {
		super.endEffect(effect);
	}
}