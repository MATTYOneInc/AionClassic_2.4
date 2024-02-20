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
package com.aionemu.gameserver.model.templates.item.actions;

import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.item.ItemChargeService;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import java.util.Collection;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChargeItemAction")
public class ChargeAction extends AbstractItemAction
{
	@XmlAttribute
	protected int capacity;
	
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		if (player.getController().isInCombat() || player.isAttackMode()) {
			///You cannot use %1 while in combat.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_SKILL_ITEM_RESTRICTED_AREA(new DescriptionId(2800159), parentItem.getNameId()));
			return false;
		}
		Collection<Item> conditioningItems = ItemChargeService.filterItemsToCondition(player, null, parentItem.getImprovement().getChargeWay());
		return conditioningItems.size() > 0;
	}
	
	public void act(final Player player, final Item parentItem, final Item targetItem) {
		PacketSendUtility.broadcastPacketAndReceive(player, new S_USE_ITEM(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 2000, 0, 0));
		final ItemUseObserver conditioning = new ItemUseObserver() {
			@Override
			public void abort() {
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.getObserveController().removeObserver(this);
				PacketSendUtility.sendPacket(player, new S_USE_ITEM(player.getObjectId(), targetItem.getObjectId(), targetItem.getItemTemplate().getTemplateId(), 0, 3, 0));
				ItemPacketService.updateItemAfterInfoChange(player, targetItem);
				///You have cancelled the enchanting of %0.
				PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_ENCHANT_ITEM_CANCELED(targetItem.getItemTemplate().getNameId()));
			}
		};
		player.getObserveController().attach(conditioning);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.getObserveController().removeObserver(conditioning);
				player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1);
				PacketSendUtility.broadcastPacketAndReceive(player, new S_USE_ITEM(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, 1, 0));
				Collection<Item> conditioningItems = ItemChargeService.filterItemsToCondition(player, null, parentItem.getImprovement().getChargeWay());
				ItemChargeService.chargeItems(player, conditioningItems, capacity);
			}
		}, 2000));
	}
}