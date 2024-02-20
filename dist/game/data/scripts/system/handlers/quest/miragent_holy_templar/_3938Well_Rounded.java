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
package quest.miragent_holy_templar;

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

public class _3938Well_Rounded extends QuestHandler
{
	private final static int questId = 3938;
	private final static int[] npcs = {203788, 203792, 203790, 203793, 203784, 203786, 798316, 203752, 203701};
	
	public _3938Well_Rounded() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerQuestNpc(203701).addOnQuestStart(questId);
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
			if (targetId == 203701) {
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
			if (targetId == 203701) {
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
            } if (targetId == 203788) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_7: {
						giveQuestItem(env, 152201596, 1);
						changeQuestStep(env, 1, 7, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 203792) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_7: {
						giveQuestItem(env, 152201639, 1);
						changeQuestStep(env, 2, 7, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 203790) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_7: {
						giveQuestItem(env, 152201615, 1);
						changeQuestStep(env, 3, 7, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 203793) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 4) {
							return sendQuestDialog(env, 2375);
						}
					} case STEP_TO_7: {
						giveQuestItem(env, 152201632, 1);
						changeQuestStep(env, 4, 7, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 203784) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 5) {
							return sendQuestDialog(env, 2716);
						}
					} case STEP_TO_7: {
						giveQuestItem(env, 152201644, 1);
						changeQuestStep(env, 5, 7, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 203786) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 6) {
							return sendQuestDialog(env, 3057);
						}
					} case STEP_TO_7: {
						giveQuestItem(env, 152201643, 1);
						changeQuestStep(env, 6, 7, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 798316) {
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
            } if (targetId == 203752) {
				long glossyOathStone = player.getInventory().getItemCountByItemId(186000081);
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 8) {
							return sendQuestDialog(env, 3739);
						}
					} case SET_REWARD: {
						if (glossyOathStone == 1) {
							removeQuestItem(env, 186000081, 1);
							return defaultCloseDialog(env, 8, 8, true, false, 0);
						} else {
							return sendQuestDialog(env, 3825);
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203701) {
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