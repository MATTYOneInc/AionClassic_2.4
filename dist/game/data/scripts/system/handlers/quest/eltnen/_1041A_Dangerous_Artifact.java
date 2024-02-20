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

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.*;

/****/
/** Author Rinzler (Encom)
/****/

public class _1041A_Dangerous_Artifact extends QuestHandler
{
	private final static int questId = 1041;
	private final static int[] npcs = {203901, 204015, 203833, 278500, 204042, 700181};
	
	public _1041A_Dangerous_Artifact() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnMovieEndQuest(38, questId);
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
		return defaultOnLvlUpEvent(env, 1034, true);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 203901) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 3) {
							return sendQuestDialog(env, 1693);
						} else if (var == 6) {
							return sendQuestDialog(env, 2716);
						}
					} case STEP_TO_1: {
						playQuestMovie(env, 184);
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_3: {
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					} case STEP_TO_6: {
						changeQuestStep(env, 6, 7, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204015) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						Npc npc = (Npc) env.getVisibleObject();
						///There's no time to waste. Let's go!
						NpcShoutsService.getInstance().sendMsg(npc, 1100687, npc.getObjectId(), 0, 0);
						///Lead the way, quickly. They'll catch us!
						NpcShoutsService.getInstance().sendMsg(npc, 1100688, npc.getObjectId(), 0, 15000);
						///Is it far from here? It's hard for me to walk.
						NpcShoutsService.getInstance().sendMsg(npc, 1100689, npc.getObjectId(), 0, 30000);
						return defaultStartFollowEvent(env, (Npc) env.getVisibleObject(), 0, 1, 2);
					}
				}
			} if (targetId == 203833) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 4) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_4: {
						changeQuestStep(env, 4, 5, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 278500) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 5) {
							return sendQuestDialog(env, 2375);
						}
					} case STEP_TO_5: {
						giveQuestItem(env, 182201011, 1);
						changeQuestStep(env, 5, 6, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204042) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 7) {
							return sendQuestDialog(env, 3057);
						} else if (var == 9) {
							return sendQuestDialog(env, 3398);
						}
					} case STEP_TO_7: {
						playQuestMovie(env, 37);
						changeQuestStep(env, 7, 8, false);
						return closeDialogWindow(env);
					} case STEP_TO_8: {
						playQuestMovie(env, 38);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 700181) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 8) {
							QuestService.questTimerStart(env, 5);
							//Explosive Device Of Yuditio.
							QuestService.addNewSpawn(210020000, player.getInstanceId(), 700182, 2426.1316f, 2279.6897f, 269.7306f, (byte) 88);
							QuestService.addNewSpawn(210020000, player.getInstanceId(), 700182, 2423.3745f, 2277.1987f, 269.2467f, (byte) 118);
							QuestService.addNewSpawn(210020000, player.getInstanceId(), 700182, 2428.8508f, 2276.9858f, 269.6148f, (byte) 28);
							return useQuestObject(env, 8, 9, false, 0, 0, 0, 182201011, 1);
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203901) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 3739);
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
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (movieId == 38) {
			qs.setQuestVar(9);
			qs.setStatus(QuestStatus.REWARD);
			updateQuestStatus(env);
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
            if (var >= 2 && var < 3) {
                qs.setQuestVar(1);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
}