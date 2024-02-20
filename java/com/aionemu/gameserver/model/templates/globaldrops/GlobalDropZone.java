package com.aionemu.gameserver.model.templates.globaldrops;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Wnkrz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalDropZone")
public class GlobalDropZone
{
	@XmlAttribute(name = "zone", required = true)
	protected String zone;
	
	public String getZone() {
		return zone;
	}
}