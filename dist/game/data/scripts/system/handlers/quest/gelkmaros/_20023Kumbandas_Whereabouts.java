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

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

/****/
/** Author Rinzler (Encom)
/****/

public class _20023Kumbandas_Whereabouts extends QuestHandler
{
    private final static int questId = 20023;
    private final static int[] npcs = {799226, 799292, 204057, 730243, 799513, 799514, 799515, 799516, 799341, 700706};
	private final static int[] IDTemple_Up_FanaticWiQ_53_An = {216592};
	
    public _20023Kumbandas_Whereabouts() {
        super(questId);
    }
	
    @Override
    public void register() {
		qe.registerOnLogOut(questId);
        qe.registerOnLevelUp(questId);
        qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: IDTemple_Up_FanaticWiQ_53_An) {
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
        int var = qs.getQuestVarById(0);
		int var1 = qs.getQuestVarById(1);
		int var2 = qs.getQuestVarById(2);
		int var3 = qs.getQuestVarById(3);
		int var4 = qs.getQuestVarById(4);
        int targetId = env.getTargetId();
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (targetId == 799226) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        } else if (var == 3) {
						    return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_1: {
                        changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_4: {
                        changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					}
                }
			} if (targetId == 799292) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						} else if (var == 2) {
							return sendQuestDialog(env, 1693);
						} else if (var == 5) {
							return sendQuestDialog(env, 2716);
						} else if (var == 11) {
							return sendQuestDialog(env, 1608);
						}
					} case STEP_TO_2: {
                        changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					} case STEP_TO_6: {
                        giveQuestItem(env, 182207612, 1);
						changeQuestStep(env, 5, 6, false);
						removeQuestItem(env, 182207611, 1);
						return closeDialogWindow(env);
					} case SET_REWARD: {
					    return defaultCloseDialog(env, 11, 11, true, false);
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
			} if (targetId == 204057) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 4) {
							return sendQuestDialog(env, 2375);
						}
					} case STEP_TO_5: {
						giveQuestItem(env, 182207611, 1);
						changeQuestStep(env, 4, 5, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 730243) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
                        if (var == 6) {
                            return sendQuestDialog(env, 3057);
                        }
					} case STEP_TO_7: {
						if (player.getInventory().getItemCountByItemId(182207612) > 0) {
							removeQuestItem(env, 182207612, 1);
							changeQuestStep(env, 6, 7, false);
							WorldMapInstance udasTemple = InstanceService.getNextAvailableInstance(300150000);
							InstanceService.registerPlayerWithInstance(udasTemple, player);
							TeleportService2.teleportTo(player, 300150000, udasTemple.getInstanceId(), 561.0000f, 223.0000f, 134.0000f, (byte) 91);
							return closeDialogWindow(env);
						}
					}
                }
			} if (targetId == 799513) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 7 && var2 == 1 && var3 == 1 && var4 == 1) {
							qs.setQuestVar(8);
							updateQuestStatus(env);
							playQuestMovie(env, 442);
							Npc npc = (Npc) env.getVisibleObject();
						    npc.getController().onDelete();
							QuestService.addNewSpawn(300150000, player.getInstanceId(), 216592, 561.0000f, 188.0000f, 135.0000f, (byte) 30);
							return closeDialogWindow(env);
						} else {
							changeQuestStep(env, 0, 1, false, 1);
							Npc npc = (Npc) env.getVisibleObject();
						    npc.getController().onDelete();
						}
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 799514) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 7 && var1 == 1 && var3 == 1 && var4 == 1) {
							qs.setQuestVar(8);
							updateQuestStatus(env);
							playQuestMovie(env, 442);
							Npc npc = (Npc) env.getVisibleObject();
						    npc.getController().onDelete();
							QuestService.addNewSpawn(300150000, player.getInstanceId(), 216592, 561.0000f, 188.0000f, 135.0000f, (byte) 30);
							return closeDialogWindow(env);
						} else {
							changeQuestStep(env, 0, 1, false, 2);
							Npc npc = (Npc) env.getVisibleObject();
						    npc.getController().onDelete();
						}
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 799515) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 7 && var1 == 1 && var2 == 1 && var4 == 1) {
							qs.setQuestVar(8);
							updateQuestStatus(env);
							playQuestMovie(env, 442);
							Npc npc = (Npc) env.getVisibleObject();
						    npc.getController().onDelete();
							QuestService.addNewSpawn(300150000, player.getInstanceId(), 216592, 561.0000f, 188.0000f, 135.0000f, (byte) 30);
							return closeDialogWindow(env);
						} else {
							changeQuestStep(env, 0, 1, false, 3);
							Npc npc = (Npc) env.getVisibleObject();
						    npc.getController().onDelete();
						}
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 799516) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 7 && var1 == 1 && var2 == 1 && var3 == 1) {
							qs.setQuestVar(8);
							updateQuestStatus(env);
							playQuestMovie(env, 442);
							Npc npc = (Npc) env.getVisibleObject();
						    npc.getController().onDelete();
							QuestService.addNewSpawn(300150000, player.getInstanceId(), 216592, 561.0000f, 188.0000f, 135.0000f, (byte) 30);
							return closeDialogWindow(env);
						} else {
							changeQuestStep(env, 0, 1, false, 4);
							Npc npc = (Npc) env.getVisibleObject();
						    npc.getController().onDelete();
						}
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 799341) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 9) {
							return sendQuestDialog(env, 4080);
						}
					} case STEP_TO_10: {
						giveQuestItem(env, 182207613, 1);
                        changeQuestStep(env, 9, 10, false);
						Npc npc = (Npc) env.getVisibleObject();
						npc.getController().onDelete();
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 700706) {
				switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 10) {
							TeleportService2.teleportTo(env.getPlayer(), 220070000, 2272.0000f, 1603.0000f, 310.0000f, (byte) 117);
							return useQuestObject(env, 10, 11, false, 0, 0, 0, 182207613, 1);
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
			if (var == 8) {
				int var1 = qs.getQuestVarById(1);
				if (var1 >= 0 && var1 < 0) {
					return defaultOnKillEvent(env, IDTemple_Up_FanaticWiQ_53_An, var1, var1 + 1, 1);
				} else if (var1 == 0) {
					qs.setQuestVar(9);
					updateQuestStatus(env);
					QuestService.addNewSpawn(300150000, player.getInstanceId(), 799341, 561.0000f, 188.0000f, 135.0000f, (byte) 30);
					return true;
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
            if (var >= 7 && var < 10) {
                qs.setQuestVar(6);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
}