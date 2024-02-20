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
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;

/**
 * @author kosyachok
 * @author -Nemesiss-
 */
public class S_CHANGE_WAREHOUSE_ITEM_DESC extends AionServerPacket {

	private Player player;
	private Item item;
	private int warehouseType;
	private ItemUpdateType updateType;

	public S_CHANGE_WAREHOUSE_ITEM_DESC(Player player, Item item, int warehouseType, ItemUpdateType updateType) {
		this.player = player;
		this.item = item;
		this.warehouseType = warehouseType;
		this.updateType = updateType;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		ItemTemplate itemTemplate = item.getItemTemplate();

		writeD(item.getObjectId());
		writeC(warehouseType);
		writeNameId(itemTemplate.getNameId());

		ItemInfoBlob itemInfoBlob = new ItemInfoBlob(player, item);
		itemInfoBlob.addBlobEntry(ItemBlobType.GENERAL_INFO);
		itemInfoBlob.writeMe(getBuf());

		if (updateType.isSendable())
			writeH(updateType.getMask());
	}
}
