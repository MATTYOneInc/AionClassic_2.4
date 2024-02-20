package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.skillengine.model.DispelCategoryType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTargetSlot;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractDispelEffect")
public class AbstractDispelEffect extends EffectTemplate {

	@XmlAttribute
	protected int dpower;

	@XmlAttribute
	protected int power;

	@XmlAttribute(name = "dispel_level")
	protected int dispelLevel;

	public void applyEffect(Effect effect) {
	}

	public void applyEffect(Effect effect, DispelCategoryType type, SkillTargetSlot slot) {
		boolean isItemTriggered = effect.getItemTemplate() != null;
		int count = value + delta * effect.getSkillLevel();
		int finalPower = power + dpower * effect.getSkillLevel();

		effect.getEffected().getEffectController().removeEffectByDispelCat(type, slot, count, dispelLevel, finalPower, isItemTriggered);
	}
}
