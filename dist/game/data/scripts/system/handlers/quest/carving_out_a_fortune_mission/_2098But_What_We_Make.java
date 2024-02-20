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
package quest.carving_out_a_fortune_mission;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;

/****/
/** Author Rinzler (Encom)
/****/

public class _2098But_What_We_Make extends QuestHandler
{
	private final static int questId = 2098;
	private final static int[] npcs = {203550, 204361, 204408, 205198, 204805, 204808, 203546, 204387,
	205190, 204207, 204301, 205155, 204784, 278001, 204053};
	
	public _2098But_What_We_Make() {
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
		return defaultOnLvlUpEvent(env, 2097, true);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 203550) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case STEP_TO_1: {
						return defaultCloseDialog(env, 0, 1, 182207089, 1, 0, 0);
					}
				}
			} if (targetId == 204361) {
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
			} if (targetId == 204408) {
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
			} if (targetId == 205198) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_4: {
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204805) {
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
			} if (targetId == 204808) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 5) {
							return sendQuestDialog(env, 2716);
						}
					} case STEP_TO_6: {
						return defaultCloseDialog(env, 5, 6, 182207090, 1, 182207089, 1);
					}
				}
			} if (targetId == 203546) {
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
			} if (targetId == 204387) {
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
			} if (targetId == 205190) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 8) {
							return sendQuestDialog(env, 3739);
						}
					} case STEP_TO_9: {
						changeQuestStep(env, 8, 9, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204207) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 9) {
							return sendQuestDialog(env, 4080);
						}
					} case STEP_TO_10: {
						return defaultCloseDialog(env, 9, 10, 182207091, 1, 182207090, 1);
					}
				}
			} if (targetId == 204301) {
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
			} if (targetId == 205155) {
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
			} if (targetId == 204784) {
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
			} if (targetId == 278001) {
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
			} if (targetId == 204053) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 14) {
							return sendQuestDialog(env, 2972);
						}
					} case SET_REWARD: {
						return defaultCloseDialog(env, 14, 14, true, false, 182207092, 1, 182207091, 1);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203550) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					removeQuestItem(env, 182207092, 1);
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}