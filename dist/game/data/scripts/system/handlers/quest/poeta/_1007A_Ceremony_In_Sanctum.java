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
package quest.poeta;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/****/
/** Author Rinzler (Encom)
/****/

public class _1007A_Ceremony_In_Sanctum extends QuestHandler
{
	private final static int questId = 1007;
	
	public _1007A_Ceremony_In_Sanctum() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerQuestNpc(790001).addOnTalkEvent(questId);
		qe.registerQuestNpc(203725).addOnTalkEvent(questId);
		qe.registerQuestNpc(203752).addOnTalkEvent(questId);
		qe.registerQuestNpc(203758).addOnTalkEvent(questId);
		qe.registerQuestNpc(203759).addOnTalkEvent(questId);
		qe.registerQuestNpc(203760).addOnTalkEvent(questId);
		qe.registerQuestNpc(203761).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1006, true);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();
		PlayerClass playerClass = PlayerClass.getStartingClassFor(player.getCommonData().getPlayerClass());
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 790001) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						TeleportService2.teleportTo(env.getPlayer(), 110010000, 1313.0000f, 1512.0000f, 568.0000f, (byte) 0);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203725) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					} case SELECT_ACTION_1353: {
						return playQuestMovie(env, 92);
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203752) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						}
					} case SELECT_ACTION_1694: {
						return playQuestMovie(env, 91);
					} case STEP_TO_3: {
						if (var == 2) {
							switch (playerClass) {
								case WARRIOR:
									qs.setQuestVar(10);
								break;
								case SCOUT:
									qs.setQuestVar(20);
								break;
								case MAGE:
									qs.setQuestVar(30);
								break;
								case PRIEST:
									qs.setQuestVar(40);
								break;
							}
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							ThreadPoolManager.getInstance().schedule(new Runnable() {
								@Override
								public void run() {
									//reward auto
									QuestRewardService.rewardMission1007(player, 1007);
								}
							}, 10000);
							return sendQuestSelectionDialog(env);
						}
					}
				}
			}
		}
		/*TO DO FIX!!!
		else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203758 && var == 10) {
				switch (env.getDialogId()) {
					case -1:
						return sendQuestDialog(env, 2034);
					case 1009:
						return sendQuestDialog(env, 5);
					case 8:
					case 9:
					case 10:
					case 11:
					case 12:
					case 13:
					case 14:
					case 15:
					case 16:
					case 18:
					if (QuestService.finishQuest(env, 0)) {
						return sendQuestSelectionDialog(env);
					}
				}
			} else if (targetId == 203759 && var == 20) {
				switch (env.getDialogId()) {
					case -1:
						return sendQuestDialog(env, 2375);
					case 1009:
						return sendQuestDialog(env, 6);
					case 8:
					case 9:
					case 10:
					case 11:
					case 12:
					case 13:
					case 14:
					case 15:
					case 16:
					case 18:
					if (QuestService.finishQuest(env, 1)) {
						return sendQuestSelectionDialog(env);
					}
				}
			} else if (targetId == 203760 && var == 30) {
				switch (env.getDialogId()) {
					case -1:
						return sendQuestDialog(env, 2716);
					case 1009:
						return sendQuestDialog(env, 7);
					case 8:
					case 9:
					case 10:
					case 11:
					case 12:
					case 13:
					case 14:
					case 15:
					case 16:
					case 18:
					if (QuestService.finishQuest(env, 2)) {
						return sendQuestSelectionDialog(env);
					}
				}
			} else if (targetId == 203761 && var == 40) {
				switch (env.getDialogId()) {
					case -1:
						return sendQuestDialog(env, 3057);
					case 1009:
						return sendQuestDialog(env, 8);
					case 8:
					case 9:
					case 10:
					case 11:
					case 12:
					case 13:
					case 14:
					case 15:
					case 16:
					case 18:
					if (QuestService.finishQuest(env, 3)) {
						return sendQuestSelectionDialog(env);
					}
				}
			}
		}*/
		return false;
	}
}