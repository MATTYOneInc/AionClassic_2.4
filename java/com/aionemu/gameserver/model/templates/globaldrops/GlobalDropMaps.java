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
@XmlType(name = "GlobalDropMaps")
public class GlobalDropMaps
{
	@XmlElement(name = "gd_map")
	protected List<GlobalDropMap> gdMaps;
	
	public List<GlobalDropMap> getGlobalDropMaps() {
		if (gdMaps == null) {
			gdMaps = new ArrayList<GlobalDropMap>();
		}
		return this.gdMaps;
	}
}