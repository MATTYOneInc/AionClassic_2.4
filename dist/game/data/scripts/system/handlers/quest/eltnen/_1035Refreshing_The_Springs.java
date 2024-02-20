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

public class _1035Refreshing_The_Springs extends QuestHandler
{
	private final static int questId = 1035;
	private final static int[] npcs = {203917, 203992, 203965, 203968, 203987, 203934, 700158, 700159, 700160};
	
	public _1035Refreshing_The_Springs() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnQuestTimerEnd(questId);
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
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 203917) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					} case STEP_TO_1: {
						playQuestMovie(env, 180);
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_2: {
						changeQuestStep(env, 4, 5, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203992) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						} else if (var == 3) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					} case STEP_TO_3: {
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203965) {
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
			} if (targetId == 203968) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 5) {
							return sendQuestDialog(env, 2375);
						}
					} case STEP_TO_5: {
						changeQuestStep(env, 5, 6, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203987) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 6) {
							return sendQuestDialog(env, 2716);
						} else if (var == 8) {
							return sendQuestDialog(env, 3057);
						}
					} case STEP_TO_6: {
						if (var == 6) {
							changeQuestStep(env, 6, 7, false);
							QuestService.questTimerStart(env, 180);
							return closeDialogWindow(env);
						} else if (var == 7) {
							return closeDialogWindow(env);
						} else if (var == 6) {
							giveQuestItem(env, 182201024, 1);
							changeQuestStep(env, 6, 7, false);
							QuestService.questTimerStart(env, 180);
						    return closeDialogWindow(env);
						}
					} case STEP_TO_7: {
						giveQuestItem(env, 182201025, 1);
						changeQuestStep(env, 8, 9, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 700158) { //Laquepin Life Stone.
				long lifeBead = player.getInventory().getItemCountByItemId(182201014);
				switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 2 && lifeBead == 1) {
							return useQuestObject(env, 2, 3, false, 0, 0, 0, 182201014, 1);
						} else if (var == 2 && lifeBead == 0) {
							return closeDialogWindow(env);
						}
					}
                }
			} if (targetId == 700159) { //Temple Life Stone.
				switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 10) {
							return useQuestObject(env, 10, 11, false, 0, 0, 0, 182201025, 1);
						}
					}
                }
			} if (targetId == 700160) { //Desert Life Stone.
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 7) {
							QuestService.questTimerEnd(env);
							return useQuestObject(env, 7, 8, false, 0, 0, 0, 182201024, 1, 31);
						}
					}
				}
			} if (targetId == 203934) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 9) {
							return sendQuestDialog(env, 3398);
						} else if (var == 11) {
							return sendQuestDialog(env, 3739);
						}
					} case STEP_TO_8: {
						if (var == 9) {
							changeQuestStep(env, 9, 10, false);
						    return closeDialogWindow(env);
						} else if (var == 11) {
							return defaultCloseDialog(env, 11, 11, true, false);
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203917) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 4080);
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
    public boolean onLogOutEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var >= 7 && var < 8) {
                qs.setQuestVar(6);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
	
	@Override
	public boolean onQuestTimerEndEvent(QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			removeQuestItem(env, 182201024, 1);
			changeQuestStep(env, 7, 6, false);
			player.getController().updateZone();
			player.getController().updateNearbyQuests();
			return true;
		}
		return false;
	}
}