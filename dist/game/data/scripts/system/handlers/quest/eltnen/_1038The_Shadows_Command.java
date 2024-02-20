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
package quest.eltnen;

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

public class _1038The_Shadows_Command extends QuestHandler
{
	private final static int questId = 1038;
	private final static int[] npcs = {203933, 203991, 700162, 700172};
	private final static int[] hippolyta = {204005};
	
	public _1038The_Shadows_Command() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnMovieEndQuest(35, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: hippolyta) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 700162) {
				switch (env.getDialog()) {
				    case USE_OBJECT: {
						if (var == 0) {
							QuestService.addNewSpawn(210020000, 1, 204025, player.getX(), player.getY(), player.getZ(), (byte) 0); //Protector Kheldon.
							return useQuestObject(env, 0, 1, false, 0, 34);
						}
					}
				}
			} if (targetId == 203933) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						} else if (var == 3) {
							return sendQuestDialog(env, 1694);
						} else if (var == 4) {
							return sendQuestDialog(env, 2034);
						} else if (var == 5) {
							return sendQuestDialog(env, 2035);
						}
					} case CHECK_COLLECTED_ITEMS: {
						return checkQuestItems(env, 4, 5, false, 2035, 2120);
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					} case STEP_TO_3: {
						removeQuestItem(env, 182201007, 1);
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					} case STEP_TO_4: {
						removeQuestItem(env, 182201015, 1);
						removeQuestItem(env, 182201016, 1);
						removeQuestItem(env, 182201017, 1);
						changeQuestStep(env, 5, 6, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 700172) {
				switch (env.getDialog()) {
				    case USE_OBJECT: {
						if (var == 2) {
							return useQuestObject(env, 2, 3, false, 0, 182201007, 1, 0, 0);
						}
					}
				}
			} if (targetId == 203991) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 6) {
							return sendQuestDialog(env, 2375);
						}
					} case STEP_TO_5: {
						playQuestMovie(env, 35);
						changeQuestStep(env, 6, 7, false);
						return closeDialogWindow(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203991) {
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
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 7) {
				int var1 = qs.getQuestVarById(1);
				if (var1 >= 0 && var1 < 0) {
					return defaultOnKillEvent(env, hippolyta, var1, var1 + 1, 1);
				} else if (var1 == 0) {
					qs.setStatus(QuestStatus.REWARD);
				    updateQuestStatus(env);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		if (movieId == 35) {
			QuestService.addNewSpawn(210020000, 1, 204005, 1768.0000f, 924.0000f, 422.0000f, (byte) 0);
			return true;
		}
		return false;
	}
	
	@Override
    public boolean onLogOutEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var >= 7) {
                qs.setQuestVar(6);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
}