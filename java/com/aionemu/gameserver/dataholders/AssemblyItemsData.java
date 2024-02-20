package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.item.AssemblyItem;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "item" })
@XmlRootElement(name = "assembly_items")
public class AssemblyItemsData {

	@XmlElement(required = true)
	protected List<AssemblyItem> item;

	@XmlTransient
	private List<AssemblyItem> items = new ArrayList<AssemblyItem>();

	void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
		for (AssemblyItem template : item)
			items.add(template);
	}

	public int size() {
		return items.size();
	}

	public AssemblyItem getAssemblyItem(int itemId) {
		for (AssemblyItem assemblyItem : items) {
			if (assemblyItem.getId() == itemId) {
				return assemblyItem;
			}
		}
		return null;
	}
}
