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
package quest.inggison;

import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

/****/
/** Author Rinzler (Encom)
/****/

public class _10023Sullas_Startling_Discovery extends QuestHandler
{
	private final static int questId = 10023;
	private final static int[] npcs = {798928, 798975, 798981, 730226, 730227, 730228, 798513, 798225, 798979, 798990, 730295, 730229};
	private final static int[] LF4_Temple_Nepilim_NamedQ_53_An = {216531};
	
	public _10023Sullas_Startling_Discovery() {
		super(questId);
	}
	
	@Override
	public void register() {
        qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182206614, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: LF4_Temple_Nepilim_NamedQ_53_An) {
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
			if (targetId == 798928) {
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
			} if (targetId == 798975) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						} else if (var == 6) {
							return sendQuestDialog(env, 3057);
						} else if (var == 9) {
							return sendQuestDialog(env, 4080);
						}
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					} case STEP_TO_7: {
						changeQuestStep(env, 6, 7, false);
						removeQuestItem(env, 182206610, 1);
						removeQuestItem(env, 182206611, 1);
						removeQuestItem(env, 182206612, 1);
						return closeDialogWindow(env);
					} case STEP_TO_10: {
					    changeQuestStep(env, 9, 10, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 798981) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_3: {
						if (player.getInventory().isFull()) {
							PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_WAREHOUSE_FULL_INVENTORY);
							return false;
						} else {
							giveQuestItem(env, 182206609, 3);
							changeQuestStep(env, 2, 3, false);
						}
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 730226) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
                        if (var == 3) {
                            return sendQuestDialog(env, 2034);
                        }
					} case STEP_TO_4: {
						giveQuestItem(env, 182206610, 1);
						changeQuestStep(env, 3, 4, false);
						removeQuestItem(env, 182206609, 1);
						///I got the western sample!
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1122003, player.getObjectId(), 2));
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 730227) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
                        if (var == 4) {
                            return sendQuestDialog(env, 2375);
                        }
					} case STEP_TO_5: {
						giveQuestItem(env, 182206611, 1);
						changeQuestStep(env, 4, 5, false);
						removeQuestItem(env, 182206609, 1);
						///I got the eastern sample!
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1122004, player.getObjectId(), 2));
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 730228) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
                        if (var == 5) {
                            return sendQuestDialog(env, 2716);
                        }
					} case STEP_TO_6: {
						giveQuestItem(env, 182206612, 1);
						changeQuestStep(env, 5, 6, false);
						removeQuestItem(env, 182206609, 1);
                        ///I got the southern sample!
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1122005, player.getObjectId(), 2));
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 798513) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 7) {
							return sendQuestDialog(env, 3398);
						}
					} case STEP_TO_8: {
						changeQuestStep(env, 7, 8, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 798225) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 8) {
							return sendQuestDialog(env, 3739);
						}
					} case STEP_TO_9: {
						changeQuestStep(env, 8, 9, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 798979) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 10) {
							return sendQuestDialog(env, 1608);
						}
					} case STEP_TO_11: {
						changeQuestStep(env, 10, 11, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 798990) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 11) {
							return sendQuestDialog(env, 1949);
						}
					} case SELECT_ACTION_1951: {
						if (var == 11) {
							playQuestMovie(env, 504);
							return sendQuestDialog(env, 1951);
						}
					} case STEP_TO_12: {
						changeQuestStep(env, 11, 12, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 730295) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 12) {
							return sendQuestDialog(env, 3995);
						}
					} case STEP_TO_13: {
						if (player.getInventory().getItemCountByItemId(182206613) > 0) {
							removeQuestItem(env, 182206613, 1);
							changeQuestStep(env, 12, 13, false);
							WorldMapInstance lowerUdasTemple = InstanceService.getNextAvailableInstance(300160000);
							InstanceService.registerPlayerWithInstance(lowerUdasTemple, player);
							TeleportService2.teleportTo(player, 300160000, lowerUdasTemple.getInstanceId(), 795.0000f, 918.0000f, 149.0000f, (byte) 73);
							return closeDialogWindow(env);
						} else {
							return sendQuestDialog(env, 10001);
						}
					}
                }
			} if (targetId == 730229) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 15) {
							return sendQuestDialog(env, 2290);
						}
					} case STEP_TO_14: {
						giveQuestItem(env, 182206614, 1);
						changeQuestStep(env, 15, 16, false);
						Npc npc = (Npc) env.getVisibleObject();
						npc.getController().onDelete();
						return closeDialogWindow(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798928) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
				} else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
					removeQuestItem(env, 182206614, 1);
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
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var == 16) {
				return HandlerResult.fromBoolean(useQuestItem(env, item, 16, 16, true));
            }
        }
        return HandlerResult.FAILED;
    }
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 14) {
				switch (targetId) {
					case 216531: {
						qs.setQuestVar(15);
						updateQuestStatus(env);
						QuestService.addNewSpawn(300160000, player.getInstanceId(), 730229, 739.0000f, 873.0000f, 153.0000f, (byte) 25);
						return true;
					}
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
            if (var >= 13 && var < 16) {
                qs.setQuestVar(12);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
}