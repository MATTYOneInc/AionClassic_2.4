package com.aionemu.gameserver.model.templates.quest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InventoryItems", propOrder = { "inventoryItem" })
public class InventoryItems {

	@XmlElement(name = "inventory_item")
	protected List<InventoryItem> inventoryItem;

	public List<InventoryItem> getInventoryItem() {
		if (inventoryItem == null) {
			inventoryItem = new ArrayList<InventoryItem>();
		}
		return inventoryItem;
	}
}
