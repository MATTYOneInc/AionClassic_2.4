package com.aionemu.gameserver.model.templates.globaldrops;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

import javax.xml.bind.annotation.*;

/**
 * @author Wnkrz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GlobalDropItem")
public class GlobalDropItem
{
	@XmlAttribute(name = "id", required = true)
	protected int itemId;
	
	@XmlTransient
	private ItemTemplate template;
	
	public int getId() {
		return itemId;
	}
	
	public ItemTemplate getItemTemplate() {
		if (template == null) {
			template = DataManager.ITEM_DATA.getItemTemplate(itemId);
		}
		return template;
	}
}