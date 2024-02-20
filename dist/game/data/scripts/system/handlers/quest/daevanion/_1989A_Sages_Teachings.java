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
package quest.daevanion;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;

/****/
/** Author Rinzler (Encom)
/****/

public class _1989A_Sages_Teachings extends QuestHandler
{
	private final static int questId = 1989;
	private final static int[] npcs = {203771, 203704, 203705, 203706, 203707, 800751};
	
	public _1989A_Sages_Teachings() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnMovieEndQuest(105, questId);
		qe.registerQuestNpc(203771).addOnQuestStart(questId);
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
			if (targetId == 203771) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						return sendQuestDialog(env, 1011);
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
			PlayerClass playerClass = player.getCommonData().getPlayerClass();
			if (targetId == 203704) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0 && playerClass == PlayerClass.GLADIATOR || playerClass == PlayerClass.TEMPLAR) {
							return sendQuestDialog(env, 1352);
						} else {
							return sendQuestDialog(env, 1438);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 203705) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0 && playerClass == PlayerClass.ASSASSIN || playerClass == PlayerClass.RANGER) {
							return sendQuestDialog(env, 1693);
						} else {
							return sendQuestDialog(env, 1779);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 203706) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0 && playerClass == PlayerClass.SORCERER || playerClass == PlayerClass.SPIRIT_MASTER) {
							return sendQuestDialog(env, 2034);
						} else {
							return sendQuestDialog(env, 2120);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 203707) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0 && playerClass == PlayerClass.CLERIC || playerClass == PlayerClass.CHANTER) {
							return sendQuestDialog(env, 2375);
						} else {
							return sendQuestDialog(env, 2461);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 800751) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 0 && playerClass == PlayerClass.THUNDERER) {
							return sendQuestDialog(env, 2546);
						} else {
							return sendQuestDialog(env, 2631);
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 203771) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 2716);
						} else if (var == 2) {
							return sendQuestDialog(env, 3057);
						} else if (var == 3) {
							if (player.getCommonData().getDp() >= 4000) {
								return sendQuestDialog(env, 3398);
							} else {
								return sendQuestDialog(env, 3484);
							}
						} else if (var == 4) {
							if (player.getCommonData().getDp() >= 4000) {
								return sendQuestDialog(env, 3739);
							} else {
								return sendQuestDialog(env, 3825);
							}
						}
					} case STEP_TO_2: {
						qs.setQuestVarById(0, 2);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					} case STEP_TO_4: {
						qs.setQuestVarById(0, 3);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					} case STEP_TO_5: {
						qs.setQuestVarById(0, 4);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					} case SELECT_REWARD: {
						if (var == 3) {
							playQuestMovie(env, 105);
							return closeDialogWindow(env);
						} else if (var == 4) {
							playQuestMovie(env, 105);
							return closeDialogWindow(env);
						}
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203771) {
                if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					return sendQuestDialog(env, 5);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onMovieEndEvent(final QuestEnv env, int movieId) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (movieId == 105) {
			player.getCommonData().setDp(0);
			qs.setStatus(QuestStatus.REWARD);
			updateQuestStatus(env);
			return true;
		}
		return false;
	}
}