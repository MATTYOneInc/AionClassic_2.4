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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/****/
/** Author Rinzler (Encom)
/****/

public class _2007Wheres_Rae_This_Time extends QuestHandler
{
	private final static int questId = 2007;
	private final static int[] npcs = {203516, 203519, 203539, 203552, 203554, 700085, 700086, 700087};
	
	public _2007Wheres_Rae_This_Time() {
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
		int[] quests = {2100, 2001, 2002, 2003, 2004, 2005, 2006};
		return defaultOnLvlUpEvent(env, quests, true);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 203516) {
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
			} if (targetId == 203519) {
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
			} if (targetId == 203539) {
				switch (env.getDialog()) {
					case START_DIALOG: {
					    if (var == 2) {
						    return sendQuestDialog(env, 1693);
					    }
					} case STEP_TO_3: {
						playQuestMovie(env, 55);
					    changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203552) {
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
			} if (targetId == 203554) {
				switch (env.getDialog()) {
					case START_DIALOG: {
					    if (var == 4) {
						    return sendQuestDialog(env, 2375);
						} else if (var == 8) {
							return sendQuestDialog(env, 2716);
						}
					} case STEP_TO_5: {
					    changeQuestStep(env, 4, 5, false);
						return closeDialogWindow(env);
					} case STEP_TO_6: {
						qs.setQuestVar(8);
                        qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 700085) { //Green Power Generator.
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 5) {
							return useQuestObject(env, 5, 6, false, false);
						}
					}
                }
            } if (targetId == 700086) { //Blue Power Generator.
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 6) {
							return useQuestObject(env, 6, 7, false, false);
						}
					}
                }
            } if (targetId == 700087) { //Violet Power Generator.
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 7) {
							return useQuestObject(env, 7, 8, false, false);
						}
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203516) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 3057);
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