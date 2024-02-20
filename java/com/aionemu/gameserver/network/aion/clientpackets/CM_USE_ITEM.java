package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.battle_pass.BattlePassAction;
import com.aionemu.gameserver.model.templates.item.actions.*;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.player.BattlePassService;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.ArrayList;

public class CM_USE_ITEM extends AionClientPacket
{
	public int uniqueItemId;
	public int type, targetItemId;
	
	public CM_USE_ITEM(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		uniqueItemId = readD();
		type = readC();
		if (type == 2) {
			targetItemId = readD();
		}
	}
	
	@Override
	protected void runImpl() {
		final Player player = getConnection().getActivePlayer();
        if (player == null || !player.isSpawned()) {
            return;
        } if (player.isProtectionActive()) {
            player.getController().stopProtectionActiveTask();
        } if (player.isCasting()) {
            player.getController().cancelCurrentSkill();
        } if (player.getController().isInShutdownProgress()) {
            return;
        } if (type == 0) {
			if (player.getController().hasTask(TaskId.ITEM_USE)) {
				player.getController().cancelUseItem();
				return;
			}
		}
		final Item item = player.getInventory().getItemByObjId(uniqueItemId);
		Item targetItem = player.getInventory().getItemByObjId(targetItemId);
		if (item == null) {
			return;
		} if (targetItem == null) {
            targetItem = player.getEquipment().getEquippedItemByObjId(targetItemId);
        } if (item.getItemTemplate().getTemplateId() == 165000001 && targetItem.getItemTemplate().canExtract()) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_ITEM_COLOR_ERROR);
			return;
		} if (!RestrictionsManager.canUseItem(player, item)) {
			return;
		} if (item.getItemTemplate().getRace() != Race.PC_ALL && item.getItemTemplate().getRace() != player.getRace()) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_USE_ITEM_INVALID_RACE);
			return;
		}
		int requiredLevel = item.getItemTemplate().getRequiredLevel(player.getCommonData().getPlayerClass());
		if (requiredLevel == -1) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_USE_ITEM_INVALID_CLASS);
			return;
		} if (requiredLevel > player.getLevel()) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_USE_ITEM_TOO_LOW_LEVEL_MUST_BE_THIS_LEVEL(item.getNameId(), requiredLevel));
			return;
		}
		HandlerResult result = QuestEngine.getInstance().onItemUseEvent(new QuestEnv(null, player, 0, 0), item);
		if (result == HandlerResult.FAILED) {
			return;
		}
		ItemActions itemActions = item.getItemTemplate().getActions();
		ArrayList<AbstractItemAction> actions = new ArrayList<AbstractItemAction>();
		if (itemActions == null) {
			return;
		} for (AbstractItemAction itemAction : itemActions.getItemActions()) {
			if (itemAction.canAct(player, item, targetItem)) {
                actions.add(itemAction);
            }
		} if (actions.size() == 0) {
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_ITEM_IS_NOT_USABLE);
            return;
        }
		// Store Item CD in server Player variable.
		// Prevents potion spamming, and relogging to use kisks/aether jelly/long CD items.
		if (player.isItemUseDisabled(item.getItemTemplate().getUseLimits())) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_ITEM_CANT_USE_UNTIL_DELAY_TIME);
			return;
		}
		int useDelay = player.getItemCooldown(item.getItemTemplate());
		if (useDelay > 0) {
			player.addItemCoolDown(item.getItemTemplate().getUseLimits().getDelayId(), System.currentTimeMillis() + useDelay, useDelay / 1000);
		}
		BattlePassService.getInstance().onUpdateBattlePassMission(player, item.getItemId(), 1, BattlePassAction.ITEM_PLAY);
		player.getObserveController().notifyItemuseObservers(item);
		for (final AbstractItemAction itemAction : actions) {
            itemAction.act(player, item, targetItem);
        }
	}
}