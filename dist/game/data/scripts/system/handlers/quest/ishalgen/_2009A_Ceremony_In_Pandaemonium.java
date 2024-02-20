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
package quest.ishalgen;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/****/
/** Author Rinzler (Encom)
/****/

public class _2009A_Ceremony_In_Pandaemonium extends QuestHandler
{
	private final static int questId = 2009;
	
	public _2009A_Ceremony_In_Pandaemonium() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerQuestNpc(203550).addOnTalkEvent(questId);
		qe.registerQuestNpc(204182).addOnTalkEvent(questId);
		qe.registerQuestNpc(204075).addOnTalkEvent(questId);
		qe.registerQuestNpc(204080).addOnTalkEvent(questId);
		qe.registerQuestNpc(204081).addOnTalkEvent(questId);
		qe.registerQuestNpc(204082).addOnTalkEvent(questId);
		qe.registerQuestNpc(204083).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env, 2008, true);
    }
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();
		PlayerClass playerClass = PlayerClass.getStartingClassFor(player.getCommonData().getPlayerClass());
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 203550) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						TeleportService2.teleportTo(env.getPlayer(), 120010000, 1685.0000f, 1400.0000f, 195.0000f, (byte) 0);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204182) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					} case SELECT_ACTION_1353: {
						if (var == 1) {
                            playQuestMovie(env, 121);
                            return false;
                        }
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204075) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						}
					} case SELECT_ACTION_1694: {
						return playQuestMovie(env, 122);
					} case STEP_TO_3: {
						if (playerClass == PlayerClass.WARRIOR) {
							qs.setQuestVar(10);
						} else if (playerClass == PlayerClass.SCOUT) {
							qs.setQuestVar(20);
						} else if (playerClass == PlayerClass.MAGE) {
							qs.setQuestVar(30);
						} else if (playerClass == PlayerClass.PRIEST) {
							qs.setQuestVar(40);
						}
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								//reward auto
								QuestRewardService.rewardMission2009(player, 2009);
							}
						}, 10000);
						return sendQuestSelectionDialog(env);
					}
				}
			}
		}
		/*TO DO FIX!!!
		else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204080 && var == 10) {
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
			} else if (targetId == 204081 && var == 20) {
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
			} else if (targetId == 204082 && var == 30) {
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
			} else if (targetId == 204083 && var == 40) {
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