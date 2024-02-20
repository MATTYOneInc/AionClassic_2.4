package com.aionemu.gameserver.model.templates.decomposable;

import com.aionemu.gameserver.model.Race;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="SelectItem")
public class SelectItem
{
	@XmlAttribute
	private int id;
	
	@XmlAttribute
	private int count = 1;
	
	@XmlAttribute(name = "race")
	protected Race race = Race.PC_ALL;
	
	public int getSelectItemId() {
		return this.id;
	}
	
	public int getCount() {
		return this.count;
	}
	
	public Race getRace() {
		return race;
	}
}