package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SpellStatus;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CloseAerialEffect")
public class CloseAerialEffect extends EffectTemplate
{
	@Override
	public void applyEffect(Effect effect) {
		effect.getEffected().getEffectController().removeEffect(8224);
		effect.getEffected().getEffectController().removeEffect(8678);
	}
	
	@Override
	public void calculate(Effect effect) {
		if (!effect.getEffected().getEffectController().isAbnormalSet(AbnormalState.OPENAERIAL)) {
            return;
        }
		super.calculate(effect, null, SpellStatus.CLOSEAERIAL);
	}
}