/*
 *  Aion Classic Emu based on Aion Encom Source Files
 *
 *  ENCOM Team based on Aion-Lighting Open Source
 *  All Copyrights : "Data/Copyrights/AEmu-Copyrights.text
 *
 *  iMPERIVM.FUN - AION DEVELOPMENT FORUM
 *  Forum: <http://https://imperivm.fun/>
 *
 */
package com.aionemu.gameserver.services.item;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ATracer
 */
public class ItemFactory {

	private static final Logger log = LoggerFactory.getLogger(ItemFactory.class);

	public static final Item newItem(int itemId) {
		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		if (itemTemplate == null) {
			log.error("Item was not populated correctly. Item template is missing for item id: " + itemId);
			return null;
		}
		return new Item(IDFactory.getInstance().nextId(), itemTemplate);
	}

	public static Item newItem(int itemId, long count) {
		Item item = newItem(itemId);
		item.setItemCount(calculateCount(item.getItemTemplate(), count));
		return item;
	}

	private static final long calculateCount(ItemTemplate itemTemplate, long count) {
		long maxStackCount = itemTemplate.getMaxStackCount();
		if (count > maxStackCount && !itemTemplate.isKinah())
			count = maxStackCount;
		return count;
	}

}
