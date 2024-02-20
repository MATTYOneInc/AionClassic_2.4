/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemAddType;

import java.util.Collections;
import java.util.List;

/**
 * @author kosyachok
 * @author -Nemesiss-
 */
public class S_ADD_WAREHOUSE extends AionServerPacket {

	private int warehouseType;
	private List<Item> items;
	private Player player;
	private ItemAddType addType;

	public S_ADD_WAREHOUSE(Item item, int warehouseType, Player player) {
		this.player = player;
		this.warehouseType = warehouseType;
		this.items = Collections.singletonList(item);
		this.addType = ItemAddType.ALL_SLOT;
	}

	public S_ADD_WAREHOUSE(Item item, int warehouseType, Player player, ItemAddType addType) {
        this(item, warehouseType, player);
        this.addType = addType;
    }

	@Override
    protected void writeImpl(AionConnection con) {
        writeC(warehouseType);
        writeH(addType.getMask());
		writeH(0); //2.7
		writeC(0); //2.7
        writeH(items.size());
        for (Item item : items)
            writeItemInfo(item);
    }

	private void writeItemInfo(Item item) {
		ItemTemplate itemTemplate = item.getItemTemplate();
		
		writeD(item.getObjectId());
		writeD(itemTemplate.getTemplateId());
		writeC(0); // some item info (4 - weapon, 7 - armor, 8 - rings, 17 - bottles)
		writeNameId(itemTemplate.getNameId());

		ItemInfoBlob itemInfoBlob = ItemInfoBlob.getFullBlob(player, item);
		itemInfoBlob.writeMe(getBuf());

		writeH((int) (item.getEquipmentSlot() & 0xFFFF));
	}
}
