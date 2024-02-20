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
package quest.ishalgen;

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
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;

/****/
/** Author Rinzler (Encom)
/****/

public class _2002Wheres_Rae extends QuestHandler
{
	private final static int questId = 2002;
	private final static int[] npcs = {203519, 203534, 203553, 700045, 203516, 203538};
	private final static int[] DaruD_4_n = {210377, 210378};
	
	public _2002Wheres_Rae() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: DaruD_4_n) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
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
		return defaultOnLvlUpEvent(env, 2100, true);
	}
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 203519) {
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
            } if (targetId == 203534) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 1) {
                            return sendQuestDialog(env, 1352);
                        }
					} case STEP_TO_2: {
						playQuestMovie(env, 52);
                        changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					}
                }
            } if (targetId == 790002) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						} else if (var == 10) {
							return sendQuestDialog(env, 2034);
						} else if (var == 11) {
							return sendQuestDialog(env, 2375);
						} else if (var == 12) {
							return sendQuestDialog(env, 2463);
						} else if (var == 13) {
							return sendQuestDialog(env, 2716);
						}
					} case STEP_TO_3: {
                        changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					} case STEP_TO_4: {
					    changeQuestStep(env, 10, 11, false);
						return closeDialogWindow(env);
					} case CHECK_COLLECTED_ITEMS: {
						if (QuestService.collectItemCheck(env, true)) {
							qs.setQuestVar(12);
							updateQuestStatus(env);
							player.getEffectController().removeEffect(18480); //Ribbit Transformation.
							///How dare you turn me into a Ribbit?
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1100644, player.getObjectId(), 2));
							return sendQuestDialog(env, 2463);
						} else {
							return sendQuestDialog(env, 2376);
						}
					} case STEP_TO_5: {
						changeQuestStep(env, 12, 99, false);
						WorldMapInstance KaramatisC = InstanceService.getNextAvailableInstance(320020000);
						InstanceService.registerPlayerWithInstance(KaramatisC, player);
						TeleportService2.teleportTo(player, 320020000, KaramatisC.getInstanceId(), 457.0000f, 426.0000f, 230.0000f);
						return closeDialogWindow(env);
					} case STEP_TO_6: {
						changeQuestStep(env, 13, 14, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 700045) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 11) {
							//Ribbit Transformation.
							SkillEngine.getInstance().getSkill(player, 18480, 1, player).useSkill();
							return closeDialogWindow(env);
						}
					}
				}
			} if (targetId == 203538) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
						if (var == 14) {
							playQuestMovie(env, 256);
							changeQuestStep(env, 14, 15, false);
							Npc npc = (Npc) env.getVisibleObject();
							npc.getController().scheduleRespawn();
							npc.getController().onDelete();
							QuestService.addNewSpawn(220010000, 1, 203553, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
							return closeDialogWindow(env);
						}
					}
                }
            } if (targetId == 205020) {
				switch (env.getDialog()) {
					case START_DIALOG: {
                        if (var == 99) {
							flyTeleport(player, 3001);
							final QuestEnv qe = env;
                            ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    changeQuestStep(qe, 99, 13, false);
									TeleportService2.teleportTo(env.getPlayer(), 220010000, 1, 940, 2295, 265, (byte) 43);
                                }
                            }, 43000);
                            return true;
                        }
                    }
				}
			} if (targetId == 203553) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        if (var == 15) {
                            return sendQuestDialog(env, 3057);
                        }
					} case STEP_TO_7: {
						Npc npc = (Npc) env.getVisibleObject();
						npc.getController().onDelete();
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					}
                }
            }
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203516) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 3398);
				} else if (env.getDialog() == QuestDialog.STEP_TO_8) {
					return sendQuestDialog(env, 5);
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
			if (var == 99) {
                qs.setQuestVar(12);
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
			if (player.getWorldId() == 320020000) {
				PacketSendUtility.sendPacket(player, new S_PLAY_MODE(1));
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int var = qs.getQuestVarById(0);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            switch (env.getTargetId()) {
                case 210377:
				case 210378:
					if (var >= 3 && var < 10) {
						qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
						updateQuestStatus(env);
						return true;
					}
				break;
            }
        }
        return false;
    }
}