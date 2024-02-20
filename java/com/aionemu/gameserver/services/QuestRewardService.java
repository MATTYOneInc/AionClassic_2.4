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
package com.aionemu.gameserver.services;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.items.ItemId;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RewardType;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.*;

import java.sql.Timestamp;
import java.util.Calendar;

public class QuestRewardService
{
	public static void rewardMission1007(Player player, int questId) {
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		Calendar calendar = Calendar.getInstance();
		Timestamp timeStamp = new Timestamp(calendar.getTime().getTime());
		PlayerClass playerClass = PlayerClass.getStartingClassFor(player.getCommonData().getPlayerClass());
		if (qs == null) {
			player.getQuestStateList().addQuest(questId, new QuestState(questId, QuestStatus.COMPLETE, 0, 1, null, 0, timeStamp));
			PacketSendUtility.sendPacket(player, new S_QUEST(questId, QuestStatus.COMPLETE.value(), 0));
		} else {
			qs.setStatus(QuestStatus.COMPLETE);
			qs.setCompleteCount(qs.getCompleteCount() + 1);
			if (playerClass == PlayerClass.WARRIOR) {
				ItemService.addItem(player, 100000652, 1);
                ItemService.addItem(player, 100900494, 1);
				ItemService.addItem(player, 101300485, 1);
			} if (playerClass == PlayerClass.SCOUT) {
				ItemService.addItem(player, 100200614, 1);
				ItemService.addItem(player, 100000652, 1);
				ItemService.addItem(player, 101700525, 1);
			} if (playerClass == PlayerClass.MAGE) {
				ItemService.addItem(player, 100600545, 1);
                ItemService.addItem(player, 100500508, 1);
			} if (playerClass == PlayerClass.PRIEST) {
				ItemService.addItem(player, 100100506, 1);
                ItemService.addItem(player, 101500506, 1);
			}
			player.getCommonData().addExp(117896, RewardType.QUEST);
			PacketSendUtility.sendPacket(player, new S_QUEST(questId, qs.getStatus(), qs.getQuestVars().getQuestVars()));
		}
	}
	
	public static void rewardMission2009(Player player, int questId) {
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		Calendar calendar = Calendar.getInstance();
		Timestamp timeStamp = new Timestamp(calendar.getTime().getTime());
		PlayerClass playerClass = PlayerClass.getStartingClassFor(player.getCommonData().getPlayerClass());
		if (qs == null) {
			player.getQuestStateList().addQuest(questId, new QuestState(questId, QuestStatus.COMPLETE, 0, 1, null, 0, timeStamp));
			PacketSendUtility.sendPacket(player, new S_QUEST(questId, QuestStatus.COMPLETE.value(), 0));
		} else {
			qs.setStatus(QuestStatus.COMPLETE);
			qs.setCompleteCount(qs.getCompleteCount() + 1);
			if (playerClass == PlayerClass.WARRIOR) {
                ItemService.addItem(player, 100000640, 1);
                ItemService.addItem(player, 100900488, 1);
                ItemService.addItem(player, 101300479, 1);
			} if (playerClass == PlayerClass.SCOUT) {
                ItemService.addItem(player, 100200605, 1);
                ItemService.addItem(player, 100000640, 1);
                ItemService.addItem(player, 101700515, 1);
			} if (playerClass == PlayerClass.MAGE) {
                ItemService.addItem(player, 100600532, 1);
                ItemService.addItem(player, 100500500, 1);
			} if (playerClass == PlayerClass.PRIEST) {
                ItemService.addItem(player, 100100495, 1);
                ItemService.addItem(player, 101500498, 1);
			}
			player.getCommonData().addExp(117896, RewardType.QUEST);
			PacketSendUtility.sendPacket(player, new S_QUEST(questId, qs.getStatus(), qs.getQuestVars().getQuestVars()));
		}
	}
}