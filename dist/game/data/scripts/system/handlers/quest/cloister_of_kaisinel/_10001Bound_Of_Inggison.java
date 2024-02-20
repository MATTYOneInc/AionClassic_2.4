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
package quest.cloister_of_kaisinel;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _10001Bound_Of_Inggison extends QuestHandler
{
	private final static int questId = 10001;
	private final static int[] npcs = {798600, 798513, 203760, 203782, 798408, 203709, 798926};
	
	public _10001Bound_Of_Inggison() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnMovieEndQuest(501, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        }
		qe.registerOnEnterZone(ZoneName.get("INGGISON_ILLUSION_FORTRESS_210050000"), questId);
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 10000, true);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
            if (targetId == 798600) {
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
            } if (targetId == 798513) {
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
            } if (targetId == 203760) {
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
            } if (targetId == 203782) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 3) {
                            return sendQuestDialog(env, 2034);
                        }
					} case STEP_TO_4: {
						giveQuestItem(env, 182206301, 1);
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 203709) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 5) {
                            return sendQuestDialog(env, 2716);
                        }
					} case STEP_TO_6: {
						giveQuestItem(env, 182206302, 1);
						changeQuestStep(env, 5, 6, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 798408) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 4) {
                            return sendQuestDialog(env, 2375);
                        } else if (var == 6) {
                            return sendQuestDialog(env, 3057);
                        } else if (var == 7) {
                            return sendQuestDialog(env, 3398);
                        }
					} case STEP_TO_5: {
						changeQuestStep(env, 4, 5, false);
						return closeDialogWindow(env);
					} case STEP_TO_7: {
						changeQuestStep(env, 6, 7, false);
						return closeDialogWindow(env);
					} case SET_REWARD: {
						removeQuestItem(env, 182206301, 1);
						removeQuestItem(env, 182206302, 1);
						TeleportService2.teleportTo(env.getPlayer(), 210050000, 1439.0000f, 407.0000f, 552.0000f, (byte) 86);
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798926) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
                } else {
                    int[] inggisonMission = {10020, 10021, 10022, 10023, 10024, 10025, 10026};
                    for (int mission: inggisonMission) {
                        QuestEngine.getInstance().onEnterZoneMissionEnd(new QuestEnv(env.getVisibleObject(), env.getPlayer(), mission, env.getDialogId()));
                    }
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
		if (movieId == 501) {
			qs.setQuestVar(8);
			qs.setStatus(QuestStatus.REWARD);
			updateQuestStatus(env);
			return true;
		}
		return false;
	}
	
	@Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
			if (zoneName == ZoneName.get("INGGISON_ILLUSION_FORTRESS_210050000")) {
				if (var == 7) {
					qs.setQuestVar(++var);
					updateQuestStatus(env);
					return playQuestMovie(env, 501);
				}
			}
		}
		return false;
	}
}