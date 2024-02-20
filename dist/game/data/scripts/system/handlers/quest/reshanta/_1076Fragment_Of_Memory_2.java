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
package quest.reshanta;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;

/****/
/** Author Rinzler (Encom)
/****/

public class _1076Fragment_Of_Memory_2 extends QuestHandler
{
    private final static int questId = 1076;
    private final static int[] npcs = {203704, 203754, 203786, 203834, 278500};
	
    public _1076Fragment_Of_Memory_2() {
        super(questId);
    }
	
    @Override
    public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnMovieEndQuest(170, questId);
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
        return defaultOnLvlUpEvent(env);
    }
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (targetId == 278500) {
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
            } if (targetId == 203834) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						} else if (var == 3) {
							return sendQuestDialog(env, 2034);
						} else if (var == 5) {
							return sendQuestDialog(env, 2716);
						}
					} case STEP_TO_2: {
						playQuestMovie(env, 102);
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					} case STEP_TO_4: {
                        final QuestEnv qe = env;
						changeQuestStep(env, 3, 4, false);
						WorldMapInstance sliverOfDarkness = InstanceService.getNextAvailableInstance(310070000);
						InstanceService.registerPlayerWithInstance(sliverOfDarkness, player);
						TeleportService2.teleportTo(player, 310070000, sliverOfDarkness.getInstanceId(), 523.0000f, 218.0000f, 1204.0000f);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								playQuestMovie(qe, 170);
								player.getController().stopProtectionActiveTask();
							}
						}, 5000);
						return closeDialogWindow(env);
                    } case STEP_TO_6: {
                        changeQuestStep(env, 5, 6, false);
						return closeDialogWindow(env);
                    }
				}
			} if (targetId == 203786) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						return sendQuestDialog(env, 1693);
					} case CHECK_COLLECTED_ITEMS: {
						if (QuestService.collectItemCheck(env, true)) {
							giveQuestItem(env, 182202006, 1);
							changeQuestStep(env, 2, 3, false);
							return sendQuestDialog(env, 10000);
						} else {
							return sendQuestDialog(env, 10001);
						}
					}
				}
			} if (targetId == 203754) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 6) {
                            return sendQuestDialog(env, 3057);
                        }
					} case SET_REWARD: {
						return defaultCloseDialog(env, 6, 6, true, false);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203704) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
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
	public boolean onMovieEndEvent(final QuestEnv env, int movieId) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (movieId == 170) {
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					qs.setQuestVar(5);
                    updateQuestStatus(env);
					removeQuestItem(env, 182202006, 1);
					TeleportService2.teleportTo(env.getPlayer(), 110010000, 2004.0000f, 1491.0000f, 581.0000f, (byte) 88);
				}
			}, 3000);
			return true;
		}
		return false;
	}
}