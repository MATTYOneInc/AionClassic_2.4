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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;

public class S_CHANGE_ITEM_DESC extends AionServerPacket
{
	private final Player player;
	private final Item item;
	private final ItemUpdateType updateType;
	
	public S_CHANGE_ITEM_DESC(Player player, Item item) {
		this(player, item, ItemUpdateType.DEC_ITEM_USE);
	}
	
	public S_CHANGE_ITEM_DESC(Player player, Item item, ItemUpdateType updateType) {
		this.player = player;
		this.item = item;
		this.updateType = updateType;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		ItemTemplate itemTemplate = item.getItemTemplate();
		writeD(item.getObjectId());
		writeNameId(itemTemplate.getNameId());
		ItemInfoBlob itemInfoBlob;
		switch (updateType) {
			case EQUIP_UNEQUIP:
				itemInfoBlob = new ItemInfoBlob(player, item);
				itemInfoBlob.addBlobEntry(ItemBlobType.EQUIPPED_SLOT);
			break;
			case CHARGE:
				itemInfoBlob = new ItemInfoBlob(player, item);
				itemInfoBlob.addBlobEntry(ItemBlobType.CONDITIONING_INFO);
			default:
				itemInfoBlob = ItemInfoBlob.getFullBlob(player, item);
			break;
		}
		itemInfoBlob.writeMe(getBuf());
		if (updateType.isSendable())
			writeH(updateType.getMask());
	}
}