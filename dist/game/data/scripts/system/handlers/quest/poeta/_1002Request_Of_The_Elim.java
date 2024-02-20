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
package quest.poeta;

import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.serverpackets.S_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.network.aion.serverpackets.S_PLAY_MODE;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;

/****/
/** Author Rinzler (Encom)
/****/

public class _1002Request_Of_The_Elim extends QuestHandler
{
	private final static int questId = 1002;
	private final static int[] npcs = {203076, 730007, 730010, 730008, 205000, 203067};
	
	public _1002Request_Of_The_Elim() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        }
		qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterWorld(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1100, true);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (targetId == 203076) {
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
            } if (targetId == 730007) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						} else if (var == 5) {
							return sendQuestDialog(env, 1693);
						} else if (var == 6) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_2: {
						giveQuestItem(env, 182200002, 1);
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					} case STEP_TO_3: {
						playQuestMovie(env, 20);
						changeQuestStep(env, 5, 6, false);
						removeQuestItem(env, 182200002, 1);
						return closeDialogWindow(env);
					} case STEP_TO_4: {
						changeQuestStep(env, 12, 13, false);
						return closeDialogWindow(env);
					} case CHECK_COLLECTED_ITEMS: {
						if (var == 6) {
							return checkQuestItems(env, 6, 12, false, 2120, 2205);
						} else if (var == 12) {
							return sendQuestDialog(env, 2120);
						}
					}
                }
            } if (targetId == 730008) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 13) {
							return sendQuestDialog(env, 2375);
						} else if (var == 14) {
							return sendQuestDialog(env, 2461);
						}
					} case STEP_TO_5: {
						changeQuestStep(env, 13, 20, false);
						WorldMapInstance KaramatisA = InstanceService.getNextAvailableInstance(310010000);
						InstanceService.registerPlayerWithInstance(KaramatisA, player);
						TeleportService2.teleportTo(player, 310010000, KaramatisA.getInstanceId(), 52.0000f, 174.0000f, 229.0000f);
						return closeDialogWindow(env);
					} case STEP_TO_6: {
						return defaultCloseDialog(env, 14, 14, true, false);
					}
				}
			} if (targetId == 730010) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 2) {
							Npc npc = (Npc) env.getVisibleObject();
							npc.getController().scheduleRespawn();
							npc.getController().onDelete();
							return useQuestObject(env, 2, 4, false, false);
						} else if (var == 4) {
							Npc npc = (Npc) env.getVisibleObject();
							npc.getController().scheduleRespawn();
							npc.getController().onDelete();
							return useQuestObject(env, 4, 5, false, false);
						}
					}
                }
            } if (targetId == 205000) {
				switch (env.getDialog()) {
					case START_DIALOG: {
                        if (var == 20) {
							flyTeleport(player, 1001);
							final QuestEnv qe = env;
                            ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    changeQuestStep(qe, 20, 14, false);
									TeleportService2.teleportTo(env.getPlayer(), 210010000, 1, 606, 1535, 115, (byte) 47);
                                }
                            }, 43000);
                            return true;
                        }
                    }
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203067) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 2716);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
        return false;
    }
	
	private void flyTeleport(Player player, int id) {
		player.setState(CreatureState.FLIGHT_TELEPORT);
		player.unsetState(CreatureState.ACTIVE);
		player.setFlightTeleportId(id);
		PacketSendUtility.sendPacket(player, new S_ACTION(player, EmotionType.START_FLYTELEPORT, id, 0));
	}
	
	@Override
    public boolean onLogOutEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
			if (var == 20) {
                qs.setQuestVar(13);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
	
	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (player.getWorldId() == 310010000) {
				PacketSendUtility.sendPacket(player, new S_PLAY_MODE(1));
				return true;
			} else {
				int var = qs.getQuestVarById(0);
				if (var == 20) {
					changeQuestStep(env, 20, 13, false);
				}
			}
		}
		return false;
	}
}