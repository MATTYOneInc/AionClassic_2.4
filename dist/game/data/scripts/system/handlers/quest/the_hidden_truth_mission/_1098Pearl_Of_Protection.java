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
package quest.the_hidden_truth_mission;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;

/****/
/** Author Rinzler (Encom)
/****/

public class _1098Pearl_Of_Protection extends QuestHandler
{
	private final static int questId = 1098;
	private final static int[] npcs = {790001, 730008, 730019, 204647, 203183, 203989, 798155, 204549,
	203752, 203164, 203917, 203996, 798176, 798212, 204535};
	
	public _1098Pearl_Of_Protection() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } 
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1097, true);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 790001) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case STEP_TO_1: {
						return defaultCloseDialog(env, 0, 1, 182206062, 1, 0, 0);
					}
				}
			} if (targetId == 730008) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 730019) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_3: {
						changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204647) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_4: {
						return defaultCloseDialog(env, 3, 4, 182206063, 1, 182206062, 1);
					}
				}
			} if (targetId == 203183) {
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
			} if (targetId == 203989) {
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
			} if (targetId == 798155) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 6) {
							return sendQuestDialog(env, 3057);
						}
					} case STEP_TO_7: {
						changeQuestStep(env, 6, 7, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204549) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 7) {
							return sendQuestDialog(env, 3398);
						}
					} case STEP_TO_8: {
						changeQuestStep(env, 7, 8, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203752) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 8) {
							return sendQuestDialog(env, 3739);
						}
					} case STEP_TO_9: {
						return defaultCloseDialog(env, 8, 9, 182206064, 1, 182206063, 1);
					}
				}
			} if (targetId == 203164) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 9) {
							return sendQuestDialog(env, 4080);
						}
					} case STEP_TO_10: {
						changeQuestStep(env, 9, 10, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203917) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 10) {
							return sendQuestDialog(env, 1608);
						}
					} case STEP_TO_11: {
						changeQuestStep(env, 10, 11, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203996) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 11) {
							return sendQuestDialog(env, 1949);
						}
					} case STEP_TO_12: {
						changeQuestStep(env, 11, 12, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 798176) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 12) {
							return sendQuestDialog(env, 2290);
						}
					} case STEP_TO_13: {
						changeQuestStep(env, 12, 13, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 798212) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 13) {
							return sendQuestDialog(env, 2631);
						}
					} case STEP_TO_14: {
						changeQuestStep(env, 13, 14, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204535) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 14) {
							return sendQuestDialog(env, 2972);
						}
					} case SET_REWARD: {
						return defaultCloseDialog(env, 14, 14, true, false, 182206065, 1, 182206064, 1);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 790001) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					removeQuestItem(env, 182206065, 1);
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}