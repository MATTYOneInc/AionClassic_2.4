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
package quest.abyss_entry;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.world.WorldMapInstance;

/****/
/** Author Rinzler (Encom)
/****/

public class _1922Deliveron_Your_Promises extends QuestHandler
{
	private final static int questId = 1922;
	private final static int[] npcs = {203830, 203901, 203764};
    private final static int[] IDLC1ArenaQ_Kuillus_25_An = {213580, 213581, 213582};
	
	public _1922Deliveron_Your_Promises() {
		super(questId);
	}
	
	@Override
	public void register() {
        qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnQuestTimerEnd(questId);
		qe.registerOnMovieEndQuest(165, questId);
		qe.registerOnMovieEndQuest(166, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		} for (int mob: IDLC1ArenaQ_Kuillus_25_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1921);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 203830) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 4) {
							return sendQuestSelectionDialog(env);
						}
					} case STEP_TO_12: {
						changeQuestStep(env, 0, 4, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203764) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 4) {
							return sendQuestDialog(env, 1693);
						} else if (qs.getQuestVarById(4) == 10) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_3: {
						changeQuestStep(env, 4, 5, false);
						WorldMapInstance sanctumUndergroundArena = InstanceService.getNextAvailableInstance(310080000);
						InstanceService.registerPlayerWithInstance(sanctumUndergroundArena, player);
						TeleportService2.teleportTo(player, 310080000, sanctumUndergroundArena.getInstanceId(), 275.0000f, 284.0000f, 161.0000f, (byte) 90);
						return closeDialogWindow(env);
					} case STEP_TO_4: {
						changeQuestStep(env, 5, 7, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203901) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 7) {
							return sendQuestDialog(env, 3739);
						}
					} case SELECT_REWARD: {
						if (var == 7) {
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return sendQuestDialog(env, 6);
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203901) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (movieId == 165) {
			QuestService.questTimerStart(env, 240);
			return true;
		} else if (movieId == 166) {
			TeleportService2.teleportTo(env.getPlayer(), 110010000, 1461.0000f, 1324.0000f, 554.0000f, (byte) 21);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (qs.getQuestVarById(0) == 5) {
                if (qs.getQuestVarById(4) < 9) {
                    return defaultOnKillEvent(env, IDLC1ArenaQ_Kuillus_25_An, 0, 9, 4);
                } else if (qs.getQuestVarById(4) == 9) {
                    defaultOnKillEvent(env, IDLC1ArenaQ_Kuillus_25_An, 9, 10, 4);
                    QuestService.questTimerEnd(env);
                    return true;
                }
			}
		}
		return false;
	}
	
	@Override
	public boolean onQuestTimerEndEvent(QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var4 = qs.getQuestVarById(4);
			if (var4 < 10) {
				qs.setQuestVar(4);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}
	
	@Override
    public boolean onLogOutEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVars().getQuestVars();
			if (var >= 5 && var < 10) {
                qs.setQuestVar(4);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
}