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
package quest.theobomos;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/****/
/** Author Rinzler (Encom)
/****/

public class _3088InCider_Trading extends QuestHandler
{
	private final static int questId = 3088;
	
	public _3088InCider_Trading() {
		super(questId);
	}
	
	public void register() {
		qe.registerQuestNpc(798202).addOnQuestStart(questId);
		qe.registerQuestNpc(798202).addOnTalkEvent(questId);
		qe.registerQuestNpc(798201).addOnTalkEvent(questId);
		qe.registerQuestNpc(798204).addOnTalkEvent(questId);
		qe.registerQuestNpc(798132).addOnTalkEvent(questId);
		qe.registerQuestNpc(798166).addOnTalkEvent(questId);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798202) {
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
			int var = qs.getQuestVarById(0);
			long melaJuice = player.getInventory().getItemCountByItemId(160003020);
			long prisonInmateTag = player.getInventory().getItemCountByItemId(182208064);
			long aquamarineEarrings = player.getInventory().getItemCountByItemId(182208049);
			if (targetId == 798202) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case SELECT_ACTION_1012: {
						if (melaJuice >= 1) {
							return sendQuestDialog(env, 1012);
						} else {
							return sendQuestDialog(env, 1267);
						}
					} case SELECT_ACTION_1097: {
						if (melaJuice >= 10) {
							return sendQuestDialog(env, 1097);
						} else {
							return sendQuestDialog(env, 1267);
						}
					} case SELECT_ACTION_1182: {
						if (melaJuice >= 100) {
							return sendQuestDialog(env, 1182);
						} else {
							return sendQuestDialog(env, 1267);
						}
					} case STEP_TO_1: {
						giveQuestItem(env, 182208049, 1);
						removeQuestItem(env, 160003020, 1);
						return sendQuestDialog(env, 1352);
					} case STEP_TO_2: {
						changeQuestStep(env, 0, 2, false);
						return closeDialogWindow(env);
					} case STEP_TO_3: {
						removeQuestItem(env, 160003020, 10);
						return sendQuestDialog(env, 2034);
					} case STEP_TO_4: {
						changeQuestStep(env, 0, 4, false);
						return closeDialogWindow(env);
					} case STEP_TO_7: {
						removeQuestItem(env, 160003020, 100);
						return sendQuestDialog(env, 3398);
					} case STEP_TO_8: {
						changeQuestStep(env, 0, 8, false);
						return closeDialogWindow(env);
					}
				}
            } if (targetId == 798201) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (aquamarineEarrings >= 1 && var == 2) {
							return sendQuestDialog(env, 1693);
						} else if (var == 5) {
							return sendQuestDialog(env, 2716);
						}
					} case STEP_TO_6: {
						changeQuestStep(env, 5, 6, false);
						return closeDialogWindow(env);
					} case SELECT_REWARD: {
						qs.setQuestVar(2);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						removeQuestItem(env, 182208049, 1);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 798204) {
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
			} if (targetId == 798132) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 6) {
							return sendQuestDialog(env, 3057);
						}
					} case SELECT_REWARD: {
						qs.setQuestVar(6);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 798166) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (prisonInmateTag >= 1) {
							return sendQuestDialog(env, 3739);
						} else {
							return sendQuestDialog(env, 3825);
						}
					} case SELECT_REWARD: {
						qs.setQuestVar(8);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						removeQuestItem(env, 182208064, 1);
						return closeDialogWindow(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798201 && qs.getQuestVarById(0) == 2) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 5);
				}
				return sendQuestEndDialog(env);
			} else if (targetId == 798132 && qs.getQuestVarById(0) == 6) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 5);
				}
				return sendQuestEndDialog(env, 1);
			} else if (targetId == 798166 && qs.getQuestVarById(0) == 8) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 5);
				}
				return sendQuestEndDialog(env, 2);
			}
		}
		return false;
	}
}