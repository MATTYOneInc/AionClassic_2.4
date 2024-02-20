package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.configs.main.GroupConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.QuestTemplate;
import com.aionemu.gameserver.model.templates.quest.QuestTargetType;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_QUEST;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CM_QUEST_SHARE extends AionClientPacket
{
	public int questId;

	public CM_QUEST_SHARE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		this.questId = readD();
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
        }
		QuestTemplate questTemplate = DataManager.QUEST_DATA.getQuestById(questId);
		if (questTemplate == null || questTemplate.isCannotShare())
			return;
		QuestState questState = player.getQuestStateList().getQuestState(this.questId);
		if ((questState == null) || (questState.getStatus() == QuestStatus.COMPLETE))
			return;
		if (player.isInGroup2()) {
			for (Player member : player.getPlayerGroup2().getOnlineMembers()) {
				if (player == member)
					continue;
				if (!MathUtil.isIn3dRange(member, player, GroupConfig.GROUP_MAX_DISTANCE)) {
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1100000, member.getName()));
					continue;
				} if (questTemplate.getTargetType().equals(QuestTargetType.FORCE)) { //Alliance.
					PacketSendUtility.sendPacket(member, new S_MESSAGE_CODE(1100005, player.getName()));
					continue;
				} if (!questTemplate.isRepeatable()) {
					if (member.getQuestStateList().getQuestState(questId) != null)
					if (member.getQuestStateList().getQuestState(questId).getStatus() != null && member.getQuestStateList().getQuestState(questId).getStatus() != QuestStatus.NONE)
					continue;
				} else {
					if (member.getQuestStateList().getQuestState(questId) != null)
					if (member.getQuestStateList().getQuestState(questId).getStatus() == QuestStatus.START || member.getQuestStateList().getQuestState(questId).getStatus() == QuestStatus.REWARD)
					continue;
				} if (member.isInFlyingState()) {
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1100003, member.getName()));
					continue;
				} if (!QuestService.checkLevelRequirement(this.questId, member.getLevel())) {
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1100003, member.getName()));
					PacketSendUtility.sendPacket(member, new S_MESSAGE_CODE(1100003, player.getName()));
					continue;
				}
				PacketSendUtility.sendPacket(member, new S_QUEST(this.questId, member.getObjectId(), true));
			}
		} else if (player.isInAlliance2()) {
			for (Player member : player.getPlayerAllianceGroup2().getOnlineMembers()) {
				if (player == member)
					continue;
					if (!MathUtil.isIn3dRange(member, player, GroupConfig.GROUP_MAX_DISTANCE)) {
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1100000, member.getName()));
					continue;
				} if (questTemplate.getTargetType().equals(QuestTargetType.UNION)) { //League.
					PacketSendUtility.sendPacket(member, new S_MESSAGE_CODE(1100005, player.getName()));
					continue;
				} if (!questTemplate.isRepeatable()) {
					if (member.getQuestStateList().getQuestState(questId) != null)
					if (member.getQuestStateList().getQuestState(questId).getStatus() != null && member.getQuestStateList().getQuestState(questId).getStatus() != QuestStatus.NONE)
					continue;
				} else {
					if (member.getQuestStateList().getQuestState(questId) != null)
					if (member.getQuestStateList().getQuestState(questId).getStatus() == QuestStatus.START || member.getQuestStateList().getQuestState(questId).getStatus() == QuestStatus.REWARD)
					continue;
				} if (member.isInFlyingState()) {
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1100003, member.getName()));
					continue;
				} if (!QuestService.checkLevelRequirement(this.questId, member.getLevel())) {
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1100003, member.getName()));
					PacketSendUtility.sendPacket(member, new S_MESSAGE_CODE(1100003, player.getName()));
					continue;
				}
				PacketSendUtility.sendPacket(member, new S_QUEST(this.questId, member.getObjectId(), true));
			}
		} else {
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1100000));
			return;
		}
	}
}