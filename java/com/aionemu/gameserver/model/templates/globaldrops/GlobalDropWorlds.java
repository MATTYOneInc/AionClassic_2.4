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
@XmlType(name = "GlobalDropWorlds")
public class GlobalDropWorlds
{
	@XmlElement(name = "gd_world")
	protected List<GlobalDropWorld> gdWorlds;
	
	public List<GlobalDropWorld> getGlobalDropWorlds() {
		if (gdWorlds == null) {
			gdWorlds = new ArrayList<GlobalDropWorld>();
		}
		return this.gdWorlds;
	}
}