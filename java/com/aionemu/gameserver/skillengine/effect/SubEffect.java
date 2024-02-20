package com.aionemu.gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubEffect")
public class SubEffect
{
	@XmlAttribute(name = "skill_id", required = true)
	private int skillId;
	
	@XmlAttribute(name = "chance", required = false)
	protected float chance = 100f;
	
	@XmlAttribute(name = "addeffect")
	private boolean addEffect = false;
	
	public int getSkillId() {
		return skillId;
	}
	
	public float getChance() {
		return chance;
	}
	
	public boolean isAddEffect() {
		return addEffect;
	}
}