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
package quest.fenris_fang;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/****/

public class _4942Proving_Proficiency extends QuestHandler
{
	private final static int questId = 4942;
	private final static int[] npcs = {204104, 204108, 204106, 204110, 204100, 204102, 798317, 204075, 204053};
	
	public _4942Proving_Proficiency() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(204053).addOnQuestStart(questId);
		for (int npc: npcs) {
		    qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204053) {
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
			if (targetId == 204053) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_2: {
						changeQuestStep(env, 0, 2, false);
						return closeDialogWindow(env);
					} case STEP_TO_3: {
						changeQuestStep(env, 0, 3, false);
						return closeDialogWindow(env);
					} case STEP_TO_4: {
						changeQuestStep(env, 0, 4, false);
						return closeDialogWindow(env);
					} case STEP_TO_5: {
						changeQuestStep(env, 0, 5, false);
						return closeDialogWindow(env);
					} case STEP_TO_6: {
						changeQuestStep(env, 0, 6, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 204104) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_7: {
						giveQuestItem(env, 152206598, 1);
						changeQuestStep(env, 1, 7, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 204108) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_7: {
						giveQuestItem(env, 152206641, 1);
						changeQuestStep(env, 2, 7, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 204106) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_7: {
						giveQuestItem(env, 152206617, 1);
						changeQuestStep(env, 3, 7, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 204110) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 4) {
							return sendQuestDialog(env, 2375);
						}
					} case STEP_TO_7: {
						giveQuestItem(env, 152206634, 1);
						changeQuestStep(env, 4, 7, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 204100) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 5) {
							return sendQuestDialog(env, 2716);
						}
					} case STEP_TO_7: {
						giveQuestItem(env, 152206646, 1);
						changeQuestStep(env, 5, 7, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 204102) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 6) {
							return sendQuestDialog(env, 3057);
						}
					} case STEP_TO_7: {
						giveQuestItem(env, 152206645, 1);
						changeQuestStep(env, 6, 7, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 798317) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 7) {
							return sendQuestDialog(env, 3398);
						}
					} case CHECK_COLLECTED_ITEMS: {
						if (QuestService.collectItemCheck(env, true)) {
							changeQuestStep(env, 7, 8, false);
							return sendQuestDialog(env, 10000);
						} else {
							return sendQuestDialog(env, 10001);
						}
					} case FINISH_DIALOG: {
						return sendQuestSelectionDialog(env);
					}
                }
            } if (targetId == 204075) {
				long glowingHolyWater = player.getInventory().getItemCountByItemId(186000085);
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 8) {
							return sendQuestDialog(env, 3739);
						}
					} case SET_REWARD: {
						if (glowingHolyWater == 1) {
							removeQuestItem(env, 186000085, 1);
							return defaultCloseDialog(env, 8, 8, true, false, 0);
						} else {
							return sendQuestDialog(env, 3825);
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204053) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}