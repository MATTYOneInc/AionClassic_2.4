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
package com.aionemu.gameserver.model.items;

import com.aionemu.gameserver.dataholders.loadingutils.adapters.NpcEquipmentList;
import com.aionemu.gameserver.dataholders.loadingutils.adapters.NpcEquippedGearAdapter;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


/**
 * @author Luno
 */
@XmlJavaTypeAdapter(NpcEquippedGearAdapter.class)
public class NpcEquippedGear implements Iterable<Entry<ItemSlot, ItemTemplate>> {

	private Map<ItemSlot, ItemTemplate> items;
	private short mask;

	private NpcEquipmentList v;

	public NpcEquippedGear(NpcEquipmentList v) {
		this.v = v;
	}

	/**
	 * @return short
	 */
	public short getItemsMask() {
		if (items == null)
			init();
		return mask;
	}

	@Override
	public Iterator<Entry<ItemSlot, ItemTemplate>> iterator() {
		if (items == null)
			init();
		return items.entrySet().iterator();
	}

	/**
	 * Here NPC equipment mask is initialized. All NPC slot masks should be lower than 65536
	 */
	public void init() {
		synchronized (this) {
			if (items == null) {
				items = new TreeMap<ItemSlot, ItemTemplate>();
				for (ItemTemplate item : v.items) {
					ItemSlot[] itemSlots = ItemSlot.getSlotsFor(item.getItemSlot());
					for (ItemSlot itemSlot : itemSlots) {
						if (items.get(itemSlot) == null) {
							items.put(itemSlot, item);
							mask |= itemSlot.getSlotIdMask();
							break;
						}
					}
				}
			}
			v = null;
		}
	}

	/**
	 * @param itemSlot
	 * @return
	 */
	public ItemTemplate getItem(ItemSlot itemSlot) {
		return items != null ? items.get(itemSlot) : null;
	}

}
