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

import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _20025Quest_For_Siels_Relics extends QuestHandler
{
    private final static int questId = 20025;
    private final static int[] npcs = {799225, 799226, 799341, 798800, 204182, 799239, 204837, 799327, 799328};
	
    public _20025Quest_For_Siels_Relics() {
        super(questId);
    }
	
    @Override
    public void register() {
		qe.registerOnLogOut(questId);
        qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182207619, questId);
        qe.registerOnMovieEndQuest(447, questId);
        qe.registerOnMovieEndQuest(555, questId);
        qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        }
		qe.registerOnEnterZone(ZoneName.get("BESHMUNDIRS_WALK_1_600010000"), questId);
    }
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 20024, true);
	}
	
	@Override
    public boolean onDialogEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (targetId == 799225) {
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
			} if (targetId == 799226) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						} else if (var == 4) {
							return sendQuestDialog(env, 2375);
						} else if (var == 9) {
							return sendQuestDialog(env, 4080);
						}
					} case STEP_TO_2: {
                        changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					} case STEP_TO_5: {
						changeQuestStep(env, 4, 5, false);
						return closeDialogWindow(env);
					} case STEP_TO_10: {
						changeQuestStep(env, 9, 10, false);
						return closeDialogWindow(env);
					}
                }
			} if (targetId == 799341) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						} else if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_3: {
						playQuestMovie(env, 555);
						return closeDialogWindow(env);
					} case STEP_TO_4: {
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 798800) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 5) {
							return sendQuestDialog(env, 2716);
						}
					} case STEP_TO_6: {
						changeQuestStep(env, 5, 6, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204182) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 6) {
							return sendQuestDialog(env, 3057);
						}
					} case STEP_TO_7: {
						giveQuestItem(env, 182207617, 1);
						changeQuestStep(env, 6, 7, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 799239) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 7) {
							return sendQuestDialog(env, 3398);
						}
					} case STEP_TO_8: {
						removeQuestItem(env, 182207617, 1);
						changeQuestStep(env, 7, 8, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204837) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 8) {
							return sendQuestDialog(env, 3739);
						}
					} case CHECK_COLLECTED_ITEMS: {
						if (QuestService.collectItemCheck(env, true)) {
							qs.setQuestVar(9);
							updateQuestStatus(env);
							giveQuestItem(env, 182207619, 1);
							return sendQuestDialog(env, 10000);
						} else {
							return sendQuestDialog(env, 10001);
						}
					}
                }
			} if (targetId == 799327) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 10) {
							return sendQuestDialog(env, 1267);
						}
					} case SELECT_ACTION_1268: {
						if (var == 10) {
						    playQuestMovie(env, 557);
							return sendQuestDialog(env, 1268);
						}
					} case STEP_TO_11: {
						changeQuestStep(env, 10, 11, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 799328) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 11) {
							return sendQuestDialog(env, 1608);
						}
					} case STEP_TO_12: {
						changeQuestStep(env, 11, 12, false);
						return closeDialogWindow(env);
					}
				}
			}
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799225) {
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
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (movieId == 555) {
			qs.setQuestVar(3);
			updateQuestStatus(env);
			return true;
		} else if (movieId == 447) {
			qs.setStatus(QuestStatus.REWARD);
			updateQuestStatus(env);
			//Tiamat's Stormer.
			QuestService.addNewSpawn(300170000, player.getInstanceId(), 799343, player.getX() + 5, player.getY(), player.getZ(), (byte) 0);
			QuestService.addNewSpawn(300170000, player.getInstanceId(), 799343, player.getX(), player.getY() + 5, player.getZ(), (byte) 0);
			QuestService.addNewSpawn(300170000, player.getInstanceId(), 799343, player.getX() - 5, player.getY(), player.getZ(), (byte) 0);
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
			if (zoneName == ZoneName.get("BESHMUNDIRS_WALK_1_600010000")) {
				if (var == 12) {
					qs.setQuestVar(13);
					updateQuestStatus(env);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
    public HandlerResult onItemUseEvent(QuestEnv env, final Item item) {
        final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() != QuestStatus.START) {
            return HandlerResult.UNKNOWN;
        }
        int var = qs.getQuestVarById(0);
        final int id = item.getItemTemplate().getTemplateId();
        if (id == 182207619) {
            if (var == 13 && player.isInsideZone(ZoneName.get("AETHERIC_FIELD_ROOM_300170000"))) {
				playQuestMovie(env, 447);
				removeQuestItem(env, 182207619, 1);
				return HandlerResult.SUCCESS;
            }
        }
        return HandlerResult.FAILED;
    }
	
	@Override
    public boolean onLogOutEvent(QuestEnv env) {
        final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var >= 12 && var < 13) {
                qs.setQuestVar(11);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
}