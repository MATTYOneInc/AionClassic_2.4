package com.aionemu.gameserver.questEngine.handlers.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Monster")
public class Monster
{
	@XmlAttribute(name = "var", required = true)
	protected int var;
	
	@XmlAttribute(name = "start_var")
	protected Integer startVar;
	
	@XmlAttribute(name = "end_var", required = true)
	protected int endVar;
	
	@XmlAttribute(name="npc_ids", required=true)
	protected List<Integer> npcIds;
	
	public int getVar() {
		return var;
	}
	
	public Integer getStartVar() {
		return startVar;
	}
	
	public int getEndVar() {
		return endVar;
	}
	
	public List<Integer> getNpcIds() {
		return npcIds;
	}
}