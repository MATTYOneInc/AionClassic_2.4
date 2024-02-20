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
import com.aionemu.gameserver.model.templates.item.ItemQuality;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

/**
 * @author ATracer
 */
public class ItemInfoService {

	public static final ItemQuality getQuality(int itemId) {
		return getItemTemplate(itemId).getItemQuality();
	}

	public static final int getNameId(int itemId) {
		return getItemTemplate(itemId).getNameId();
	}

	public static final ItemTemplate getItemTemplate(int itemId) {
		return DataManager.ITEM_DATA.getItemTemplate(itemId);
	}
}
