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
package quest.gelkmaros;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _20020Crash_Of_The_Dredgion extends QuestHandler
{
    private final static int questId = 20020;
	private final static int[] npcs = {799225, 799226, 799239, 798703};
	
	public _20020Crash_Of_The_Dredgion() {
        super(questId);
    }
	
    @Override
    public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerOnMovieEndQuest(552, questId);
		qe.registerOnMovieEndQuest(559, questId);
		qe.registerQuestItem(182207600, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        }
		qe.registerOnEnterZone(ZoneName.get("COWARDS_COVE_220070000"), questId);
    }
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 20026, true);
	}
	
    @Override
    public boolean onDialogEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (targetId == 799225) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        }
					} case SELECT_ACTION_1012: {
						if (var == 0) {
							playQuestMovie(env, 556);
							return sendQuestDialog(env, 1012);
						}
					} case STEP_TO_1: {
                        changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 799226) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 1) {
                            return sendQuestDialog(env, 1352);
                        } else if (var == 2) {
                            return sendQuestDialog(env, 1693);
                        } else if (var == 5) {
                            return sendQuestDialog(env, 2716);
                        } else if (var == 7) {
                            return sendQuestDialog(env, 3398);
                        }
					} case STEP_TO_2: {
                        changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					} case STEP_TO_6: {
						changeQuestStep(env, 5, 6, false);
						return closeDialogWindow(env);
					} case STEP_TO_8: {
						playQuestMovie(env, 552);
						return closeDialogWindow(env);
					} case CHECK_COLLECTED_ITEMS: {
						return checkQuestItems(env, 2, 3, false, 10000, 10001);
					} case FINISH_DIALOG: {
						if (var == 3) {
							defaultCloseDialog(env, 3, 3);
						} else if (var == 2) {
							defaultCloseDialog(env, 2, 2);
						}
					}
                }
            } if (targetId == 799239) {
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
            } if (targetId == 798703) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 4) {
                            return sendQuestDialog(env, 2375);
                        }
					} case STEP_TO_5: {
						giveQuestItem(env, 182207600, 1);
                        changeQuestStep(env, 4, 5, false);
						return closeDialogWindow(env);
					}
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799226) {
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
    public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() != QuestStatus.START) {
            return HandlerResult.UNKNOWN;
        }
        int var = qs.getQuestVarById(0);
        int id = item.getItemTemplate().getTemplateId();
        if (id == 182207600) {
            if (var == 6 && player.isInsideZone(ZoneName.get("DREDGION_CRASH_SITE_220070000"))) {
				qs.setQuestVar(7);
				updateQuestStatus(env);
				removeQuestItem(env, 182207600, 1);
				QuestService.addNewSpawn(220070000, player.getInstanceId(), 700700, player.getX(), player.getY(), player.getZ(), (byte) 0);
				return HandlerResult.SUCCESS;
            }
        }
        return HandlerResult.FAILED;
    }
	
	@Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
			if (zoneName == ZoneName.get("COWARDS_COVE_220070000")) {
				if (var == 8) {
					playQuestMovie(env, 559);
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
		if (movieId == 552) {
			qs.setQuestVar(8);
			updateQuestStatus(env);
			return true;
		} else if (movieId == 559) {
			qs.setQuestVar(8);
			qs.setStatus(QuestStatus.REWARD);
			updateQuestStatus(env);
			return true;
		}
		return false;
	}
}