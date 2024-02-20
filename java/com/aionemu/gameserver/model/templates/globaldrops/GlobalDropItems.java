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
@XmlType(name = "GlobalDropItems")
public class GlobalDropItems
{
	@XmlElement(name = "gd_item")
	protected List<GlobalDropItem> gdItems;
	
	public List<GlobalDropItem> getGlobalDropItems() {
		if (gdItems == null) {
			gdItems = new ArrayList<GlobalDropItem>();
		}
		return this.gdItems;
	}
}