package com.aionemu.gameserver.model.templates.npc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MassiveLooting")
public class MassiveLooting
{
	@XmlAttribute
	protected int itemid;
	
	@XmlAttribute(name = "looting_num")
	protected int lootingNum;
	
	@XmlAttribute(name = "min_level")
	protected int minLevel;
	
	@XmlAttribute(name = "max_level")
	protected int maxLevel;
	
	public int getLootingNum() {
		return lootingNum;
	}
	
	public int getItemid() {
		return itemid;
	}
	
	public int getMinLevel() {
		return minLevel;
	}
	
	public int getMaxLevel() {
		return maxLevel;
	}
}