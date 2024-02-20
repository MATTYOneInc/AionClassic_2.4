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
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

/****/
/** Author Rinzler (Encom)
/****/
 
public class _20021The_Aether_Must_Flow extends QuestHandler
{
	private final static int questId = 20021;
	private final static int[] npcs = {799226, 799247, 799250, 799325, 799503, 799258, 799239};
	private final static int[] DF4_Slime_DR_52_An = {215992, 215994};
	private final static int[] DF4_Foam_DR_52_An = {215995};
	private final static int[] IDElim_3F_KomadeNM_53_Ae = {215488};
	
	public _20021The_Aether_Must_Flow() {
		super(questId);
	}
	
	@Override
	public void register() {
        qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182207603, questId);
		qe.registerQuestItem(182207604, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		} for (int mob: DF4_Slime_DR_52_An) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: DF4_Foam_DR_52_An) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} for (int mob: IDElim_3F_KomadeNM_53_Ae) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 20020, true);
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
			} if (targetId == 799247) {
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
			} if (targetId == 799250) {
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
			} if (targetId == 799325) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 2034);
						} else if (var == 4) {
							return sendQuestDialog(env, 2120);
						} else if (var == 5) {
							return sendQuestDialog(env, 2716);
						}
					} case STEP_TO_4: {
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					} case STEP_TO_6: {
						if (player.getLevel() > 51) {
							return sendQuestDialog(env, 27);
						} else {
							WorldMapInstance talocHollow = InstanceService.getNextAvailableInstance(300190000);
							InstanceService.registerPlayerWithInstance(talocHollow, player);
							TeleportService2.teleportTo(player, 300190000, talocHollow.getInstanceId(), 202.0000f, 226.0000f, 1098.0000f, (byte) 30);
							changeQuestStep(env, 5, 6, false);
							return closeDialogWindow(env);
						}
					}
				}
			} if (targetId == 799503) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 9) {
							return sendQuestDialog(env, 4080);
						}
					} case STEP_TO_10: {
						changeQuestStep(env, 9, 10, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 799258) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 10) {
							return sendQuestDialog(env, 1267);
						}
					} case STEP_TO_11: {
						changeQuestStep(env, 10, 11, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 799239) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 11) {
							return sendQuestDialog(env, 1608);
						}
					} case SET_REWARD: {
						return defaultCloseDialog(env, 11, 11, true, false);
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
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (player.getWorldId() == 300190000) {
				int itemId = item.getItemId();
				int var = qs.getQuestVarById(0);
				if (itemId == 182207604) {
					if (var == 6) {
						qs.setQuestVar(7);
						updateQuestStatus(env);
						return HandlerResult.SUCCESS;
					}
				} else if (itemId == 182207603) {
					if (var == 7) {
						int var3 = qs.getQuestVarById(3);
						if (var3 >= 0 && var3 < 19) {
							changeQuestStep(env, var3, var3 + 1, false, 3);
							return HandlerResult.SUCCESS;
						} else if (var3 == 19) {
							qs.setQuestVar(8);
							updateQuestStatus(env);
							return HandlerResult.SUCCESS;
						}
					}
				}
			}
		}
		return HandlerResult.UNKNOWN;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 4) {
				int targetId = env.getTargetId();
				int var1 = qs.getQuestVarById(1);
				int var2 = qs.getQuestVarById(2);
				switch (targetId) {
					case 215992:
					case 215994:
						if (var1 < 9) {
							return defaultOnKillEvent(env, DF4_Slime_DR_52_An, 0, 9, 1);
						} else if (var1 == 9) {
							if (var2 == 10) {
								qs.setQuestVar(5);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, DF4_Slime_DR_52_An, 9, 10, 1);
							}
						}
					break;
					case 215995:
						if (var2 < 9) {
							return defaultOnKillEvent(env, DF4_Foam_DR_52_An, 0, 9, 2);
						} else if (var2 == 9) {
							if (var1 == 10) {
								qs.setQuestVar(5);
								updateQuestStatus(env);
								return true;
							} else {
								return defaultOnKillEvent(env, DF4_Foam_DR_52_An, 9, 10, 2);
							}
						}
					break;
				}
			} else if (var == 8) {
				int var1 = qs.getQuestVarById(1);
                if (var1 >= 0 && var1 < 0) {
                    return defaultOnKillEvent(env, IDElim_3F_KomadeNM_53_Ae, var1, var1 + 1, 1);
                } else if (var1 == 0) {
					qs.setQuestVar(9);
					updateQuestStatus(env);
					playQuestMovie(env, 437);
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
            if (var >= 6 && var < 9) {
                qs.setQuestVar(5);
                updateQuestStatus(env);
				removeQuestItem(env, 182207603, 1);
				removeQuestItem(env, 182207604, 1);
                return true;
            }
        }
        return false;
    }
}