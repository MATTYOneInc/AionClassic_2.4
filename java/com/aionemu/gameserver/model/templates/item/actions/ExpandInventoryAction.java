package com.aionemu.gameserver.model.templates.item.actions;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.S_USE_ITEM;
import com.aionemu.gameserver.services.CubeExpandService;
import com.aionemu.gameserver.services.WarehouseService;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExpandInventoryAction")
public class ExpandInventoryAction extends AbstractItemAction
{
	@XmlAttribute(name = "level")
	private int level;
	
	@XmlAttribute(name = "storage")
	private StorageType storage;

	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		switch (storage) {
			case CUBE:
				return CubeExpandService.canExpand(player);
			case WAREHOUSE:
				return WarehouseService.canExpand(player);
		}
		return false;
	}

	@Override
	public void act(Player player, Item parentItem, Item targetItem) {
		if (!player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1))
			return;
		ItemTemplate itemTemplate = parentItem.getItemTemplate();
		PacketSendUtility.broadcastPacket(player,
				new S_USE_ITEM(player.getObjectId(), parentItem.getObjectId(), itemTemplate.getTemplateId()), true);

		switch (storage) {
			case CUBE:
				CubeExpandService.expand(player, false);
				break;
			case WAREHOUSE:
				WarehouseService.expand(player);
				break;
		}
	}

}