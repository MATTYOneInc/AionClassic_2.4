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
import com.aionemu.gameserver.world.zone.ZoneName;

/****/
/** Author Rinzler (Encom)
/****/

public class _20022Spreading_Asmodae_Reach extends QuestHandler
{
    private final static int questId = 20022;
	private final static int[] npcs = {799226, 799282, 700701, 700702};
	private final static int[] DF4_DrakanWi_Re_53_An = {216102, 216103};
	
	public _20022Spreading_Asmodae_Reach() {
        super(questId);
    }
	
    @Override
    public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182207609, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: DF4_DrakanWi_Re_53_An) {
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        }
    }
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 20021, true);
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
            } if (targetId == 799282) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 1) {
                            return sendQuestDialog(env, 1352);
                        } else if (var == 2) {
                            return sendQuestDialog(env, 1693);
                        } else if (var == 4) {
                            return sendQuestDialog(env, 2375);
                        } else if (var == 7) {
                            return sendQuestDialog(env, 3398);
                        } else if (var == 9) {
						    return sendQuestDialog(env, 4080);
						}
					} case STEP_TO_2: {
                        changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					} case STEP_TO_5: {
						giveQuestItem(env, 182207608, 2);
						changeQuestStep(env, 4, 5, false);
						return closeDialogWindow(env);
					} case STEP_TO_8: {
						giveQuestItem(env, 182207609, 1);
						changeQuestStep(env, 7, 8, false);
						return closeDialogWindow(env);
					} case SET_REWARD: {
						return defaultCloseDialog(env, 9, 9, true, false);
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
            } if (targetId == 700701) {
				switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 5) {
							return useQuestObject(env, 5, 6, false, 0, 0, 0, 182207608, 1);
						}
					}
                }
			} if (targetId == 700702) {
				switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 6) {
							return useQuestObject(env, 6, 7, false, 0, 0, 0, 182207608, 1);
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
			if (var == 3) {
				int var1 = qs.getQuestVarById(1);
				if (var1 >= 0 && var1 < 4) {
					return defaultOnKillEvent(env, DF4_DrakanWi_Re_53_An, var1, var1 + 1, 1);
				} else if (var1 == 4) {
					qs.setQuestVar(4);
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
        if (id == 182207609) {
            if (var == 8 && player.isInsideZone(ZoneName.get("DF4_ITEMUSEAREA_Q20022"))) {
				qs.setQuestVar(9);
				updateQuestStatus(env);
				playQuestMovie(env, 553);
				removeQuestItem(env, 182207609, 1);
				return HandlerResult.SUCCESS;
            }
        }
        return HandlerResult.FAILED;
    }
}