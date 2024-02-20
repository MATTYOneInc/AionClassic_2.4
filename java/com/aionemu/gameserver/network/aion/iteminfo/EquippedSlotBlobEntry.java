package com.aionemu.gameserver.network.aion.iteminfo;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob.ItemBlobType;

import java.nio.ByteBuffer;

public class EquippedSlotBlobEntry extends ItemBlobEntry
{
	EquippedSlotBlobEntry() {
		super(ItemBlobType.EQUIPPED_SLOT);
	}
	
	@Override
	public void writeThisBlob(ByteBuffer buf) {
		Item item = ownerItem;
		writeD(buf, item.isEquipped() ? item.getEquipmentSlot() : 0);
	}
	
	@Override
	public int getSize() {
		return 4;
	}
}