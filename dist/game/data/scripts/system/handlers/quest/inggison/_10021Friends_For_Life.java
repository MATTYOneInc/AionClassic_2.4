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
 
public class _10021Friends_For_Life extends QuestHandler
{
	private final static int questId = 10021;
	private final static int[] npcs = {730008, 730019, 730024, 798927, 798954, 799022};
	private final static int[] LF4_A3_BrohumBFi = {215520, 215521, 215522, 215523};
	
	public _10021Friends_For_Life() {
		super(questId);
	}
	
	@Override
	public void register() {
        qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182206627, questId);
		qe.registerQuestItem(182206628, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		} for (int mob: LF4_A3_BrohumBFi) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 10020, true);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 798927) {
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
			} if (targetId == 798954) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);					
						} else if (var == 8) {
							return sendQuestDialog(env, 3057);
						}
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					} case SET_REWARD: {
						return defaultCloseDialog(env, 8, 8, true, false);
					}
				}
			} if (targetId == 799022) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1779);
						} else if (var == 4) {
							return sendQuestDialog(env, 2375);
						} else if (var == 7) {
							return sendQuestDialog(env, 2716);
						} else if (var == 14) {
							return sendQuestDialog(env, 4336);
						}
					} case STEP_TO_3: {
						if (var == 14) {
						    changeQuestStep(env, 14, 3, false);
							return closeDialogWindow(env);
						}
					} case STEP_TO_4: {
						if (var == 2) {
						    changeQuestStep(env, 2, 11, false);
							return closeDialogWindow(env);
						}
					} case STEP_TO_5: {
						if (player.getLevel() > 51) {
							return sendQuestDialog(env, 27);
						} else {
							WorldMapInstance talocHollow = InstanceService.getNextAvailableInstance(300190000);
							InstanceService.registerPlayerWithInstance(talocHollow, player);
							TeleportService2.teleportTo(player, 300190000, talocHollow.getInstanceId(), 202.0000f, 226.0000f, 1098.0000f, (byte) 30);
							changeQuestStep(env, 4, 5, false);
							return closeDialogWindow(env);
						}
					} case CHECK_COLLECTED_ITEMS: {
						return checkQuestItems(env, 7, 8, false, 10000, 10001);
					} case FINISH_DIALOG: {
						if (var == 8) {
							defaultCloseDialog(env, 8, 8);
						} else if (var == 7) {
							defaultCloseDialog(env, 7, 7);
						}
					}
				}
			} if (targetId == 730008) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 11) {
							return sendQuestDialog(env, 3398);					
						}
					} case STEP_TO_8: {
						///Daminu blessed me!
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1122000, player.getObjectId(), 2));
						changeQuestStep(env, 11, 12, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 730019) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 12) {
							return sendQuestDialog(env, 3739);					
						}
					} case STEP_TO_9: {
						///Lodas blessed me!
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1122001, player.getObjectId(), 2));
						changeQuestStep(env, 12, 13, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 730024) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 13) {
							return sendQuestDialog(env, 4080);					
						}
					} case STEP_TO_10: {
						///Trajanus blessed me!
						PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1122002, player.getObjectId(), 2));
						changeQuestStep(env, 13, 14, false);
						return closeDialogWindow(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798927) {
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
				if (var1 >= 0 && var1 < 33) {
					return defaultOnKillEvent(env, LF4_A3_BrohumBFi, var1, var1 + 1, 1);
				} else if (var1 == 33) {
					qs.setQuestVar(4);
					updateQuestStatus(env);
					return true;
				}
			} else if (var == 7) {
				int targetId = env.getTargetId();
				switch (targetId) {
					case 215488:
					    playQuestMovie(env, 437);
					break;
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
				if (itemId == 182206627) {
					if (var == 5) {
						qs.setQuestVar(6);
						updateQuestStatus(env);
						return HandlerResult.SUCCESS;
					}
				} else if (itemId == 182206628) {
					if (var == 6) {
						int var2 = qs.getQuestVarById(2);
						if (var2 >= 0 && var2 < 19) {
							changeQuestStep(env, var2, var2 + 1, false, 2);
							return HandlerResult.SUCCESS;
						} else if (var2 == 19) {
							qs.setQuestVar(7);
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
    public boolean onLogOutEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var >= 5 && var < 7) {
                qs.setQuestVar(4);
                updateQuestStatus(env);
				removeQuestItem(env, 182206627, 1);
				removeQuestItem(env, 182206628, 1);
                return true;
            }
        }
        return false;
    }
}