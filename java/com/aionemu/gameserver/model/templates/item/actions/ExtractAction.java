package com.aionemu.gameserver.model.templates.item.actions;

import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.battle_pass.BattlePassAction;
import com.aionemu.gameserver.network.aion.serverpackets.S_CHANGE_ITEM_DESC;
import com.aionemu.gameserver.network.aion.serverpackets.S_USE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.player.BattlePassService;
import com.aionemu.gameserver.services.EnchantService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtractAction")
public class ExtractAction extends AbstractItemAction
{
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		if (parentItem == null || targetItem == null) {
			///The item cannot be found.
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_ITEM_COLOR_ERROR);
			return false;
		} if (player.getInventory().isFull()) {
			///You cannot acquire the item because there is no space in the inventory.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_DICE_INVEN_ERROR);
			return false;
		} if (player.getController().isInCombat() || player.isAttackMode()) {
			///You cannot extract item while in combat.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_DECOMPOSE_ITEM_INVALID_STANCE(new DescriptionId(2800159)));
			return false;
		}
		BattlePassService.getInstance().onUpdateBattlePassMission(player, parentItem.getItemId(), 1, BattlePassAction.ITEM_PLAY);
		return true;
	}
	
	@Override
	public void act(final Player player, final Item parentItem, final Item targetItem) {
		PacketSendUtility.sendPacket(player, new S_USE_ITEM(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 2000, 0, 0));
		player.getController().cancelTask(TaskId.ITEM_USE);
		final ItemUseObserver observer = new ItemUseObserver() {
			@Override
			public void abort() {
				player.getController().cancelTask(TaskId.ITEM_USE);
				PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_DECOMPOSE_ITEM_CANCELED(parentItem.getNameId()));
				PacketSendUtility.sendPacket(player, new S_USE_ITEM(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, 2, 0));
				player.getObserveController().removeObserver(this);
			}
		};
		player.getObserveController().attach(observer);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				player.getObserveController().removeObserver(observer);
				boolean result = EnchantService.equipExtraction(player, parentItem);
				if (result) {
                    PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_DECOMPOSE_ITEM_SUCCEED(parentItem.getNameId()));
                } else {
                    PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_DECOMPOSE_ITEM_FAILED(parentItem.getNameId()));
                }
				player.getInventory().decreaseByItemId(targetItem.getItemId(), 1);
				player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1);
				PacketSendUtility.sendPacket(player, new S_USE_ITEM(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, result ? 1 : 2, 0));
				PacketSendUtility.sendPacket(player, new S_CHANGE_ITEM_DESC(player, targetItem));
			}
		}, 2000));
	}
}