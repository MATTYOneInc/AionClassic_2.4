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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/****/
/** Author Rinzler (Encom)
/****/

public class _1005Barring_The_Gate extends QuestHandler
{
	private final static int questId = 1005;
	private final static int[] npcs = {203067, 203081, 790001, 203085, 203086, 700080, 700081, 700082, 700083};
	
	public _1005Barring_The_Gate() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int npc: npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		int[] quests = {1100, 1001, 1002, 1003, 1004};
		return defaultOnLvlUpEvent(env, quests, true);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 203067) {
				switch (env.getDialog()) {
					case START_DIALOG: {
					    if (var == 0) {
						    return sendQuestDialog(env, 1011);
					    }
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203081) {
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
			} if (targetId == 790001) {
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
			} if (targetId == 203085) {
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
			} if (targetId == 203086) {
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
			} if (targetId == 700081) { //Green Power Generator.
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 5) {
							return useQuestObject(env, 5, 6, false, false);
						}
					}
                }
            } if (targetId == 700082) { //Blue Power Generator.
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 6) {
							return useQuestObject(env, 6, 7, false, false);
						}
					}
                }
            } if (targetId == 700083) { //Violet Power Generator.
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 7) {
							return useQuestObject(env, 7, 8, false, false);
						}
					}
                }
            } if (targetId == 700080) { //Poeta Abyss Gate.
				switch (env.getDialog()) {
                    case USE_OBJECT: {
						qs.setQuestVar(8);
                        qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						playQuestMovie(env, 21);
                        return closeDialogWindow(env);
					}
                }
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203067) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 2716);
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