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

public class _20024Battles_At_Vorgaltem extends QuestHandler
{
    private final static int questId = 20024;
	private final static int[] npcs = {799226, 799308, 799298, 798713, 700707};
	private final static int[] DF4_Drake_DR_54_An = {216033, 216034};
	private final static int[] DF4_DrakanFi_Re_55_An = {216101, 216104, 216107, 216108, 216109, 216112, 216448, 216449, 216450, 216451};
	
	public _20024Battles_At_Vorgaltem() {
        super(questId);
    }
	
    @Override
    public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182207615, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: DF4_Drake_DR_54_An) {
		    qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: DF4_DrakanFi_Re_55_An) {
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
            if (targetId == 799226) {
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
            } if (targetId == 799308) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 1) {
                            return sendQuestDialog(env, 1352);
                        } else if (var == 3) {
                            return sendQuestDialog(env, 2034);
                        }
					} case STEP_TO_2: {
                        changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					} case STEP_TO_4: {
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 799298) {
				switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 4) {
							return sendQuestDialog(env, 2375);
						} else if (var == 5) {
							return sendQuestDialog(env, 2716);
						} else if (var == 7) {
							return sendQuestDialog(env, 3398);
						} else if (var == 9) {
							return sendQuestDialog(env, 4080);
						} else if (var == 11) {
							return sendQuestDialog(env, 1608);
						}
					} case SELECT_ACTION_1609: {
						if (var == 11) {
							playQuestMovie(env, 554);
							return sendQuestDialog(env, 1609);
						}
					} case STEP_TO_5: {
						return defaultCloseDialog(env, 4, 5);
					} case STEP_TO_8: {
						changeQuestStep(env, 7, 8, false);
						return closeDialogWindow(env);
					} case STEP_TO_10: {
						giveQuestItem(env, 182207616, 1);
						changeQuestStep(env, 9, 10, false);
						return closeDialogWindow(env);
					} case SET_REWARD: {
						return defaultCloseDialog(env, 11, 11, true, false);
					} case CHECK_COLLECTED_ITEMS: {
						return checkQuestItems(env, 5, 6, false, 10000, 10001);
					} case FINISH_DIALOG: {
						if (var == 6) {
							defaultCloseDialog(env, 6, 6);
						} else if (var == 5) {
							defaultCloseDialog(env, 5, 5);
						}
					}
                }
			} if (targetId == 798713) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 6) {
                            return sendQuestDialog(env, 3057);
                        }
					} case STEP_TO_7: {
						giveQuestItem(env, 182207615, 1);
                        changeQuestStep(env, 6, 7, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 700707) {
				switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 10) {
							return useQuestObject(env, 10, 11, false, 0, 0, 0, 182207616, 1);
						}
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
	public boolean onKillEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 2) {
				int targetId = env.getTargetId();
				int var1 = qs.getQuestVarById(1);
				int var2 = qs.getQuestVarById(2);
				switch (targetId) {
					case 216033:
					case 216034:
						if (var1 < 9) {
							return defaultOnKillEvent(env, DF4_Drake_DR_54_An, 0, 9, 1);
						} else if (var1 == 9) {
							if (var2 == 30) {
								qs.setQuestVar(3);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, DF4_Drake_DR_54_An, 9, 10, 1);
							}
						}
					break;
					case 216101:
					case 216104:
					case 216107:
					case 216108:
					case 216109:
					case 216112:
					case 216448:
					case 216449:
					case 216450:
					case 216451:
						if (var2 < 29) {
							return defaultOnKillEvent(env, DF4_DrakanFi_Re_55_An, 0, 29, 2);
						} else if (var2 == 29) {
							if (var1 == 10) {
								qs.setQuestVar(3);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, DF4_DrakanFi_Re_55_An, 29, 30, 2);
							}
						}
					break;
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
        if (id == 182207615) {
            if (var == 8 && player.isInsideZone(ZoneName.get("DF4_ITEMUSEAREA_Q20024"))) {
				qs.setQuestVar(9);
				updateQuestStatus(env);
				removeQuestItem(env, 182207615, 1);
				//Klawtiar Guardian.
				QuestService.addNewSpawn(220070000, 1, 216661, 654.8930f, 945.2200f, 317.8254f, (byte) 99);
				QuestService.addNewSpawn(220070000, 1, 216661, 655.3670f, 940.9927f, 316.7086f, (byte) 25);
				QuestService.addNewSpawn(220070000, 1, 216661, 658.5494f, 943.5622f, 317.3889f, (byte) 62);
				QuestService.addNewSpawn(220070000, 1, 216661, 663.7776f, 936.4654f, 317.8194f, (byte) 63);
				QuestService.addNewSpawn(220070000, 1, 216661, 660.0895f, 933.5155f, 316.4141f, (byte) 21);
				QuestService.addNewSpawn(220070000, 1, 216661, 660.0674f, 938.1336f, 316.9085f, (byte) 97);
				return HandlerResult.SUCCESS;
            }
        }
        return HandlerResult.FAILED;
    }
}