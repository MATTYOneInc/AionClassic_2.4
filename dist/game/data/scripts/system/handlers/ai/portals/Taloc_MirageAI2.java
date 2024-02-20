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

@AIName("Taloc_Mirage")
public class Taloc_MirageAI2 extends PortalAI2
{
	protected int rewardDialogId = 5;
	protected int startingDialogId = 10;
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
		} if (dialogId == 1013) {
			PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getObjectId(), 1013, 0));
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
			PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(getObjectId(), teleportationDialogId, 0));
		}
	}
}