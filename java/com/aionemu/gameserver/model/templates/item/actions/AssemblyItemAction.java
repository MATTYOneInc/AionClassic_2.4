package com.aionemu.gameserver.model.templates.item.actions;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.AssemblyItem;
import com.aionemu.gameserver.network.aion.serverpackets.S_USE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AssemblyItemAction")
public class AssemblyItemAction extends AbstractItemAction
{
	@XmlAttribute
	private int item;
	
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		AssemblyItem assemblyItem = getAssemblyItem();
		if (assemblyItem == null) {
			return false;
		} for (Integer itemId: assemblyItem.getParts()) {
			if (player.getInventory().getFirstItemByItemId(itemId) == null) {
				return false;
			}
		} for (Integer itemId: assemblyItem.getParts2()) {
			if (player.getInventory().getFirstItemByItemId(itemId) == null) {
				return false;
			}
		} if (player.getInventory().isFull()) {
			//You cannot acquire the item because there is no space in the inventory.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_DICE_INVEN_ERROR);
			return false;
		}
		return true;
	}
	
	public static void removeItems(Player player, int itemId, long itemCount) {
		if (!player.getInventory().decreaseByItemId(itemId, itemCount)) {
		}
	}
	
	@Override
	public void act(final Player player, final Item parentItem, Item targetItem) {
		PacketSendUtility.broadcastPacketAndReceive(player, new S_USE_ITEM(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 1000, 0, 0));
		final ItemUseObserver observer = new ItemUseObserver() {
			@Override
			public void abort() {
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.removeItemCoolDown(parentItem.getItemTemplate().getUseLimits().getDelayId());
				//Assembly canceled.
				PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1401123));
				PacketSendUtility.broadcastPacket(player, new S_USE_ITEM(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, 2, 0), true);
				player.getObserveController().removeObserver(this);
			}
		};
		player.getObserveController().attach(observer);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				player.getObserveController().removeObserver(observer);
				player.getController().cancelTask(TaskId.ITEM_USE);
				AssemblyItem assemblyItem = getAssemblyItem();
				int itemType = 0;
				boolean validAssembly = true;
				if (validAssembly) {
					for (Integer itemId: assemblyItem.getParts()) {
						if (!player.getInventory().decreaseByItemId(itemId, assemblyItem.getPartsNum())) {
							return;
						}
						player.getInventory().decreaseByItemId(itemId, 1);
						//Assembly success.
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1401122));
						PacketSendUtility.broadcastPacketAndReceive(player, new S_USE_ITEM(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemId(), 0, validAssembly ? 1 : 2, 384));
						player.getController().cancelTask(TaskId.ITEM_USE);
						if (assemblyItem.getProcAssembly() != 0) {
							if (Rnd.get(1, 100) < 15) {
								itemType = 2;
							} else {
								itemType = 1;
							}
						} else {
							itemType = 1;
						}
					} for (Integer itemId: assemblyItem.getParts2()) {
						if (!player.getInventory().decreaseByItemId(itemId, assemblyItem.getPartsNum2())) {
							return;
						}
						player.getInventory().decreaseByItemId(itemId, 1);
						//Assembly success.
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1401122));
						PacketSendUtility.broadcastPacketAndReceive(player, new S_USE_ITEM(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemId(), 0, validAssembly ? 1 : 2, 384));
						player.getController().cancelTask(TaskId.ITEM_USE);
					}
				} switch(itemType) {
					case 0:
					break;
					case 1:
						ItemService.addItem(player, assemblyItem.getId(), 1);
					break;
					case 2:
						ItemService.addItem(player, assemblyItem.getProcAssembly(), 1);
					break;
				}
			}
		}, 1000));
	}
	
	public AssemblyItem getAssemblyItem() {
		return DataManager.ASSEMBLY_ITEM_DATA.getAssemblyItem(item);
	}
}