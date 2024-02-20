package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "Godstone")
public class GodstoneInfo
{
	@XmlAttribute
	private int skillid;
	@XmlAttribute
	private int skilllvl;
	@XmlAttribute
	private int probability;
	@XmlAttribute
	private int probabilityleft;
	@XmlAttribute
	private int breakprob;
	@XmlAttribute
	private int breakcount;
	
	public int getSkillid() {
		return skillid;
	}
	
	public int getSkilllvl() {
		return skilllvl;
	}
	
	public int getProbability() {
		return probability;
	}
	
	public int getProbabilityleft() {
		return probabilityleft;
	}
	
	public int getBreakprob() {
		return breakprob;
	}
	
	public int getBreakcount() {
		return breakcount;
	}
}