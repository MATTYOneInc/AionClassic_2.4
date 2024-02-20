package com.aionemu.gameserver.model.templates.quest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InventoryItem")
public class InventoryItem {

	@XmlAttribute(name = "item_id")
	protected Integer itemId;

	public Integer getItemId() {
		return itemId;
	}
}
