package com.aionemu.gameserver.model.templates.globaldrops;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wnkrz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalDropNpcs")
public class GlobalDropNpcs
{
	@XmlElement(name = "gd_npc")
	protected List<GlobalDropNpc> gdNpcs;
	
	public List<GlobalDropNpc> getGlobalDropNpcs() {
		if (gdNpcs == null) {
			gdNpcs = new ArrayList<GlobalDropNpc>();
		}
		return this.gdNpcs;
	}
}