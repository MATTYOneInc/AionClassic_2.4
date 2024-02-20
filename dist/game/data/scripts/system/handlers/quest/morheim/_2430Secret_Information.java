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
package quest.morheim;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/****/

public class _2430Secret_Information extends QuestHandler
{
	private final static int questId = 2430;
	
	public _2430Secret_Information() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(204327).addOnQuestStart(questId);
		qe.registerQuestNpc(204327).addOnTalkEvent(questId);
		qe.registerQuestNpc(204377).addOnTalkEvent(questId);
		qe.registerQuestNpc(205244).addOnTalkEvent(questId);
		qe.registerQuestNpc(798081).addOnTalkEvent(questId);
		qe.registerQuestNpc(798082).addOnTalkEvent(questId);
		qe.registerQuestNpc(204300).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204327) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 4762);
					} case ASK_ACCEPTION: {
						return sendQuestDialog(env, 4);
					} case ACCEPT_QUEST: {
						return sendQuestStartDialog(env);
					} case REFUSE_QUEST: {
				        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.START) {
			long kinah = player.getInventory().getKinah();
			long holyPendant = player.getInventory().getItemCountByItemId(182204222);
			int var = qs.getQuestVarById(0);
			if (targetId == 204327) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						} else if (var == 3) {
							return sendQuestDialog(env, 2034);
						} else if (var == 7) {
							return sendQuestDialog(env, 3398);
						}
					} case STEP_TO_1: {
						if (kinah >= 500) {
							QuestService.startQuest(env);
							changeQuestStep(env, 0, 1, false);
							player.getInventory().decreaseKinah(500);
							return sendQuestDialog(env, 1352);
						} else {
							return sendQuestDialog(env, 1267);
						}
					} case STEP_TO_3: {
						if (kinah >= 5000) {
							QuestService.startQuest(env);
							changeQuestStep(env, 0, 3, false);
							player.getInventory().decreaseKinah(5000);
							return sendQuestDialog(env, 2034);
						} else {
							return sendQuestDialog(env, 1267);
						}
					} case STEP_TO_7: {
						if (kinah >= 50000) {
							QuestService.startQuest(env);
							changeQuestStep(env, 0, 7, false);
							player.getInventory().decreaseKinah(50000);
							return sendQuestDialog(env, 3398);
						} else {
							return sendQuestDialog(env, 1267);
						}
					} case STEP_TO_2: {
						giveQuestItem(env, 182204221, 1);
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					} case STEP_TO_4: {
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					} case STEP_TO_8: {
						changeQuestStep(env, 7, 8, false);
						return closeDialogWindow(env);
					} case FINISH_DIALOG: {
						return sendQuestSelectionDialog(env);
					}
                }
            } if (targetId == 204377) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						}
					} case SELECT_REWARD: {
						removeQuestItem(env, 182204221, 1);
						changeQuestStep(env, 2, 2, true);
						return sendQuestDialog(env, 5);
					}
                }
            } if (targetId == 205244) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 4) {
							return sendQuestDialog(env, 2375);
						}
					} case STEP_TO_5: {
						changeQuestStep(env, 4, 5, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 798081) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 5) {
							return sendQuestDialog(env, 2716);
						}
					} case STEP_TO_6: {
						changeQuestStep(env, 5, 6, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 798082) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 6) {
							return sendQuestDialog(env, 3057);
						}
					} case SELECT_REWARD: {
						changeQuestStep(env, 6, 6, true);
						return sendQuestDialog(env, 6);
					}
                }
            } if (targetId == 204300) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (holyPendant == 1 && var == 8) {
							return sendQuestDialog(env, 3739);
						} else {
							return sendQuestDialog(env, 3825);
						}
					} case SELECT_REWARD: {
						changeQuestStep(env, 8, 8, true);
						removeQuestItem(env, 182204222, 1);
						return sendQuestDialog(env, 7);
					} case FINISH_DIALOG: {
						return sendQuestSelectionDialog(env);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			int var = qs.getQuestVarById(0);
			if (targetId == 204377) {
				if (var == 2) {
					return sendQuestEndDialog(env, 0);
				}
			} else if (targetId == 798082) {
				if (var == 6) {
					return sendQuestEndDialog(env, 1);
				}
			} else if (targetId == 204300) {
				if (var == 8) {
					return sendQuestEndDialog(env, 2);
				}
			}
		}
		return false;
	}
}