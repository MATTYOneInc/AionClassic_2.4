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

public class _2947Following_Through extends QuestHandler
{
	private final static int questId = 2947;
	private final static int[] npcs = {204053, 204301, 204089, 700268};
	private final static int[] IDDC1ArenaQ_UndeadLightFiD_25_An = {213583, 213584};
	
	public _2947Following_Through() {
		super(questId);
	}
	
	@Override
	public void register() {
        qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnQuestTimerEnd(questId);
		qe.registerOnMovieEndQuest(167, questId);
		qe.registerOnMovieEndQuest(168, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		} for (int mob: IDDC1ArenaQ_UndeadLightFiD_25_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 2946);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 204053) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case STEP_TO_12: {
						changeQuestStep(env, 0, 4, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204089) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 4) {
							return sendQuestDialog(env, 1693);
						} else if (qs.getQuestVarById(4) == 10) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_3: {
						changeQuestStep(env, 4, 5, false);
						WorldMapInstance trinielUndergroundArena = InstanceService.getNextAvailableInstance(320090000); //Triniel Underground Arena.
						InstanceService.registerPlayerWithInstance(trinielUndergroundArena, player);
						TeleportService2.teleportTo(player, 320090000, trinielUndergroundArena.getInstanceId(), 275.0000f, 284.0000f, 161.0000f, (byte) 90);
						return closeDialogWindow(env);
					} case STEP_TO_4: {
						qs.setQuestVar(9);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return defaultCloseDialog(env, 9, 9, true, false);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204301) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (movieId == 167) {
			QuestService.questTimerStart(env, 240);
			return true;
		} else if (movieId == 168) {
			TeleportService2.teleportTo(env.getPlayer(), 120010000, 982.0000f, 1552.0000f, 210.0000f, (byte) 105);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 5) {
				int var4 = qs.getQuestVarById(4);
				int[] IDDC1ArenaQ_UndeadLightFiD_25_An = {213583, 213584};
				if (var4 < 9) {
					return defaultOnKillEvent(env, IDDC1ArenaQ_UndeadLightFiD_25_An, 0, 9, 4);
				} else if (var4 == 9) {
					defaultOnKillEvent(env, IDDC1ArenaQ_UndeadLightFiD_25_An, 9, 10, 4);
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
			if (var4 != 10) {
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
            if (var >= 5 && var < 9) {
                qs.setQuestVar(4);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
}