package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.skillengine.model.DispelCategoryType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTargetSlot;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DispelNpcBuffEffect")
public class DispelNpcBuffEffect extends AbstractDispelEffect {

	public void applyEffect(Effect effect) {
		super.applyEffect(effect, DispelCategoryType.NPC_BUFF, SkillTargetSlot.BUFF);
	}
}
