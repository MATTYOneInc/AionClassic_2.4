package com.aionemu.gameserver.model.templates.stats;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "kisk_stats")
@XmlAccessorType(XmlAccessType.FIELD)
public class KiskStatsTemplate
{
	@XmlAttribute(name = "usemask")
	private int useMask = 6;
	
	@XmlAttribute(name = "members")
	private int maxMembers = 576;
	
	@XmlAttribute(name = "resurrects")
	private int maxResurrects = 1728;

	@XmlAttribute(name = "live_time")
	private int liveTime;
	
	public int getUseMask() {
		return useMask;
	}
	
	public int getMaxMembers() {
		return maxMembers;
	}
	
	public int getMaxResurrects() {
		return maxResurrects;
	}

	public int getLiveTime() {
		return liveTime;
	}
}