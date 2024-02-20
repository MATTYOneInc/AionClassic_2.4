package com.aionemu.gameserver.model.templates.spawns;

import com.aionemu.gameserver.model.TribeClass;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpawnModel")
public class SpawnModel
{
	@XmlAttribute(name = "tribe")
	private TribeClass tribe;
	
	@XmlAttribute(name = "ai")
	private String ai;
	
	public TribeClass getTribe() {
		return tribe;
	}
	
	public String getAi() {
		return ai;
	}
}