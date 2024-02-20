package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.skillengine.model.Effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RebirthEffect")
public class RebirthEffect extends EffectTemplate
{
	@XmlAttribute(name = "resurrect_percent", required = true)
	protected int resurrectPercent;
	
	@XmlAttribute(name = "skill_id")
	protected int skillId;
	
	@Override
	public void applyEffect(Effect effect) {
		//Hand Of Reincarnation.
		if (effect.getSkillId() == 1041) {
		    effect.setHandOfReincarnation(true);
		}
		effect.addToEffectedController();
	}
	
	public int getResurrectPercent() {
		return resurrectPercent;
	}
	
	public int getSkillId() {
		return skillId;
	}
}