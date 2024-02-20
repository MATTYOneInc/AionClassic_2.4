package com.aionemu.gameserver.model.templates.item.actions;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestStartAction")
public class QuestStartAction extends AbstractItemAction
{
	@XmlAttribute
	protected int questid;
	
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		QuestState qs = player.getQuestStateList().getQuestState(questid);
		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			return true;
		} if (qs.getStatus() != QuestStatus.COMPLETE) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_QUEST_ACQUIRE_ERROR_WORKING_QUEST);
		} else if (!qs.canRepeat()) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_QUEST_ACQUIRE_ERROR_NONE_REPEATABLE(DataManager.QUEST_DATA.getQuestById(questid).getName()));
		}
		return false;
	}
	
	@Override
	public void act(Player player, Item parentItem, Item targetItem) {
		QuestEngine.getInstance().onDialog(new QuestEnv(null, player, questid, QuestDialog.ASK_ACCEPTION.id()));
	}
}