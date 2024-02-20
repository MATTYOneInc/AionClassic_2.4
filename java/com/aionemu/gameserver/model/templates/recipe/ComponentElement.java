package com.aionemu.gameserver.model.templates.recipe;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * @author Ranastic
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ComponentElement")
public class ComponentElement {

	@XmlAttribute
	protected int itemid;
	@XmlAttribute
	protected int quantity;
	
	public Integer getItemid() {
		return itemid;
	}
	
	public Integer getQuantity() {
		return quantity;
	}
}
