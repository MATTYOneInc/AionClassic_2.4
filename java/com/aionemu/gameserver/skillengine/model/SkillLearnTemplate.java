package com.aionemu.gameserver.skillengine.model;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "skill")
public class SkillLearnTemplate
{
	@XmlAttribute(name = "classId", required = true)
	private PlayerClass classId = PlayerClass.ALL;
	
	@XmlAttribute(name = "skillId", required = true)
	private int skillId;
	
	@XmlAttribute(name = "skillLevel", required = true)
	private int skillLevel;
	
	@XmlAttribute(name = "name", required = true)
	private String name;
	
	@XmlAttribute(name = "race", required = true)
	private Race race;
	
	@XmlAttribute(name = "minLevel", required = true)
	private int minLevel;
	
	@XmlAttribute(name = "skill_group")
	private String skill_group;
	
	@XmlAttribute
    private boolean autoLearn;
	
	@XmlAttribute
	private boolean stigma = false;
	
	@XmlAttribute
	private boolean skillBook = false;
	
	public PlayerClass getClassId() {
		return classId;
	}
	
	public int getSkillId() {
		return skillId;
	}
	
	public int getSkillLevel() {
		return skillLevel;
	}
	
	public String getName() {
		return name;
	}
	
	public int getMinLevel() {
		return minLevel;
	}
	
	public Race getRace() {
		return race;
	}
	
	public String getSkillGroup() {
		return skill_group;
	}
	
	public boolean isAutoLearn() {
        return autoLearn;
    }
	
	public boolean isStigma() {
		return stigma;
	}
	
	public boolean isSkillBook() {
		return skillBook;
	}
}