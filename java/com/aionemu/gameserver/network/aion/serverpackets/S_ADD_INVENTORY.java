/*
 * This file is part of aion-unique <aionunique.com>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.ItemStorage;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemAddType;

import java.util.List;

public class S_ADD_INVENTORY extends AionServerPacket
{
	private final List<Item> items;
	private final int size;
	private Player player;
	private ItemAddType addType;
	
	public S_ADD_INVENTORY(List<Item> items, Player player) {
		this.player = player;
		this.items = items;
		this.size = items.size();
		this.addType = ItemAddType.ITEM_COLLECT;
	}
	
	public S_ADD_INVENTORY(List<Item> items, Player player, ItemAddType addType) {
        this(items, player);
        this.addType = addType;
    }
	
	@Override
    protected void writeImpl(AionConnection con) {
		// TODO: Rework it, who knows where it could be bugged else.
		int mask = addType.getMask();
		if (addType == ItemAddType.ITEM_COLLECT) {
			// TODO: if size != 1, then it's buy item, should not specify any slot in other places then !!!
			if (size == 1 && items.get(0).getEquipmentSlot() != ItemStorage.FIRST_AVAILABLE_SLOT)
				mask = ItemAddType.PARTIAL_WITH_SLOT.getMask();
		}
		writeH(mask);
		writeH(0); //2.7
		writeC(0); //2.7
		writeH(size); // number of entries
		for (Item item : items)
			writeItemInfo(item);
    }
	
	private void writeItemInfo(Item item) {
		ItemTemplate itemTemplate = item.getItemTemplate();
		writeD(item.getObjectId());
		writeD(itemTemplate.getTemplateId());
		writeNameId(itemTemplate.getNameId());
		ItemInfoBlob itemInfoBlob = ItemInfoBlob.getFullBlob(player, item);
		itemInfoBlob.writeMe(getBuf());

		writeH(item.isEquipped() ? 255 : item.getEquipmentSlot()); // FF FF equipment
		writeC(0x00);//isEquiped?
	}
}