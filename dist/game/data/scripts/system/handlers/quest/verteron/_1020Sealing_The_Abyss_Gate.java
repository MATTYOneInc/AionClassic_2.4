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
package quest.mission;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.QuestService;

/****/
/** Author Rinzler (Encom)
/****/

public class _1020Sealing_The_Abyss_Gate extends QuestHandler
{
	private final static int questId = 1020;
	private final static int[] npcs = {203098, 700143};
	private final static int[] NagaFightersQ_21_An = {210753};
	
	public _1020Sealing_The_Abyss_Gate() {
		super(questId);
	}
	
	@Override
	public void register() {
        qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnMovieEndQuest(29, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		} for (int mob: NagaFightersQ_21_An) {
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
            if (targetId == 203098) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        }
                    } case STEP_TO_1: {
						playQuestMovie(env, 29);
                        changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 700143) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
                        if (var == 2) {
							return useQuestObject(env, 2, 2, true, 0, 0, 0, 182200024, 1, 153);
						}
					}
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203098) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 1352);
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
            if (var == 2) {
                int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 0) {
                    return defaultOnKillEvent(env, NagaFightersQ_21_An, var1, var1 + 1, 1);
                } else if (var1 == 0) {
					giveQuestItem(env, 182200024, 1);
                    return true;
                }
            }
        }
        return false;
    }
	
	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (movieId == 29) {
			TeleportService2.teleportTo(env.getPlayer(), 210030000, 2683.2085f, 1068.8977f, 199.3750f, (byte) 0);
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
            if (var >= 1 && var < 2) {
                qs.setQuestVar(1);
                updateQuestStatus(env);
				removeQuestItem(env, 182200024, 1);
                return true;
            }
        }
        return false;
    }
}