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
package ai.portals;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.autogroup.AutoGroupType;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.portal.PortalPath;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.teleport.PortalService;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("portal_dialog")
public class PortalDialogAI2 extends PortalAI2
{
	protected int rewardDialogId = 5;
	protected int startingDialogId = 1011;
	protected int questDialogId = 10;
	
	@Override
	protected void handleDialogStart(Player player) {
		if (getTalkDelay() == 0) {
			checkDialog(player);
		} else {
			super.handleDialogStart(player);
		}
	}
	
	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		QuestEnv env = new QuestEnv(getOwner(), player, questId, dialogId);
		env.setExtendedRewardIndex(extendedRewardIndex);
		if (questId > 0 && QuestEngine.getInstance().onDialog(env)) {
			return true;
		} if (dialogId == DialogAction.INSTANCE_PARTY_MATCH.id()) {
			AutoGroupType agt = AutoGroupType.getAutoGroup(player.getLevel(), getNpcId());
			if (agt != null) {
				PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(agt.getInstanceMaskId(), 0));
			}
			PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getObjectId(), 0));
		} else if (dialogId == DialogAction.OPEN_INSTANCE_RECRUIT.id()) {
			AutoGroupType agt = AutoGroupType.getAutoGroup(player.getLevel(), getNpcId());
			if (agt != null) {
				PacketSendUtility.sendPacket(player, new S_PARTY_MATCH(0x1A, agt.getInstanceMapId()));
			}
		} else {
			if (questId == 0) {
				PortalPath portalPath = DataManager.PORTAL2_DATA.getPortalDialog(getNpcId(), dialogId, player.getRace());
				if (portalPath != null) {
					PortalService.port(portalPath, player, getObjectId());
				}
			} else {
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getObjectId(), dialogId, questId));
			}
		}
		return true;
	}
	
	@Override
	protected void handleUseItemFinish(Player player) {
		checkDialog(player);
	}
	
	private void checkDialog(Player player) {
		int npcId = getNpcId();
		int teleportationDialogId = DataManager.PORTAL2_DATA.getTeleportDialogId(npcId);
		List<Integer> relatedQuests = QuestEngine.getInstance().getQuestNpc(npcId).getOnTalkEvent();
		boolean playerHasQuest = false;
		boolean playerCanStartQuest = false;
		if (!relatedQuests.isEmpty()) {
			for (int questId : relatedQuests) {
				final QuestState qs = player.getQuestStateList().getQuestState(questId);
				if (qs != null && (qs.getStatus() == QuestStatus.START || qs.getStatus() == QuestStatus.REWARD)) {
					playerHasQuest = true;
					break;
				} else if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
					if (QuestService.checkStartConditions(new QuestEnv(getOwner(), player, questId, 0), false)) {
						playerCanStartQuest = true;
						continue;
					}
				}
			}
		} if (playerHasQuest) {
			boolean isRewardStep = false;
			for (int questId : relatedQuests) {
				final QuestState qs = player.getQuestStateList().getQuestState(questId);
				if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getObjectId(), rewardDialogId, questId));
					isRewardStep = true;
					break;
				}
			} if (!isRewardStep) {
				PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getObjectId(), questDialogId));
			}
		} else if (playerCanStartQuest) {
			PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getObjectId(), startingDialogId));
		} else {
        	switch (npcId) {
				///Steel Rake [Solo]
				case 800405: //Hikarinerk.
				    final QuestState qs3200 = player.getQuestStateList().getQuestState(3200);
					if (qs3200 == null || qs3200.getStatus() != QuestStatus.COMPLETE) {
						PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getObjectId(), 1055, 0));
						///You cannot use it as the required quest has not been completed.
						PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NEED_FINISH_QUEST);
					} else {
						PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getObjectId(), 1011, 0));
					}
				break;
				case 800406: //Midorunerk.
				    final QuestState qs4200 = player.getQuestStateList().getQuestState(4200);
					if (qs4200 == null || qs4200.getStatus() != QuestStatus.COMPLETE) {
						PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getObjectId(), 1055, 0));
						///You cannot use it as the required quest has not been completed.
						PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NEED_FINISH_QUEST);
					} else {
						PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getObjectId(), 1011, 0));
					}
				break;
				default:
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getObjectId(), teleportationDialogId, 0));
				break;
			}
		}
	}
}