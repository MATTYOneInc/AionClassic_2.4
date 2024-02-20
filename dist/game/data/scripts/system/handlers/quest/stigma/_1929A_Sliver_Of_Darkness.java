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
package quest.stigma;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;

/****/
/** Author Rinzler (Encom)
/****/

public class _1929A_Sliver_Of_Darkness extends QuestHandler
{
	private final static int questId = 1929;
	
	private final static int[] npcs = {203752, 203852, 203164, 205110, 700240, 205111, 203701, 203711};
	private final static int[] stigmaStone = {140000001};
	private final static int[] LF1B_SpectreQ_21_Ae = {212992};
	
	public _1929A_Sliver_Of_Darkness() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnDie(questId);
		qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnMovieEndQuest(155, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		} for (int stigma: stigmaStone) {
			qe.registerOnEquipItem(stigma, questId);
		} for (int mob: LF1B_SpectreQ_21_Ae) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}
	
	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}
	
    @Override
    public boolean onLvlUpEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
        if (player == null || env == null) {
            return false;
        } if (player.getRace() == Race.ELYOS && player.getLevel() >= 20 && player.getLevel() <= 55) {
            QuestService.startQuest(env);
            return true;
        }
        return false;
    }
	
	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
        int var = qs.getQuestVarById(0);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 203752) {
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
			} if (targetId == 203852) {
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
			} if (targetId == 203164) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						} else if (var == 8) {
							return sendQuestDialog(env, 3057);
						}
					} case STEP_TO_3: {
						changeQuestStep(env, 2, 93, false);
						WorldMapInstance sliverOfDarkness = InstanceService.getNextAvailableInstance(310070000);
						InstanceService.registerPlayerWithInstance(sliverOfDarkness, player);
						TeleportService2.teleportTo(player, 310070000, sliverOfDarkness.getInstanceId(), 339.0000f, 100.0000f, 1190.0000f);
						return closeDialogWindow(env);
					} case STEP_TO_7: {
						changeQuestStep(env, 8, 9, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 205110) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 93) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_4: {
						flyTeleport(player, 31001);
						changeQuestStep(env, 93, 94, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 700240) {
				switch (env.getDialog()) {
				    case USE_OBJECT: {
						if (var == 94) {
							playQuestMovie(env, 155);
							return closeDialogWindow(env);
						}
					}
				}
			} if (targetId == 205111) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 96) {
							if (isStigmaEquipped(env)) {
								return sendQuestDialog(env, 2716);
							} else {
								PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(env.getVisibleObject().getObjectId(), 1));
								return closeDialogWindow(env);
							}
						}
					} case START_DIALOG: {
						if (var == 98) {
							return sendQuestDialog(env, 2375);
						}
					} case SELECT_ACTION_2546: {
						if (var == 98) {
							if (giveQuestItem(env, getStoneId(player), 1)) {
								long stigmaShards = player.getInventory().getItemCountByItemId(141000001);
								if (stigmaShards < 300) {
									if (!player.getInventory().isFull()) {
										ItemService.addItem(player, 141000001, 300 - stigmaShards);
										PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(env.getVisibleObject().getObjectId(), 1));
										return true;
									} else if (player.getInventory().isFull()) {
										PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_WAREHOUSE_FULL_INVENTORY);
										return false;
									}
								} else {
									PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(env.getVisibleObject().getObjectId(), 1));
									return true;
								}
							}
						}
					} case SELECT_ACTION_2720: {
						if (var == 96) {
							Npc npc = (Npc) env.getVisibleObject();
							npc.getController().delete();
							changeQuestStep(env, 96, 97, false);
							QuestService.addNewSpawn(310070000, player.getInstanceId(), 212992, player.getX() + 2, player.getY(), player.getZ(), (byte) 0);
							return closeDialogWindow(env);
						}
					}
				}
			} if (targetId == 203701) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 9) {
							return sendQuestDialog(env, 3398);
						}
					} case STEP_TO_8: {
						return defaultCloseDialog(env, 9, 9, true, false);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203711) {
				if (env.getDialog() == QuestDialog.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				} else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onEquipItemEvent(final QuestEnv env, int itemId) {
		changeQuestStep(env, 98, 96, false);
		return closeDialogWindow(env);
	}
	
	private void flyTeleport(Player player, int id) {
		player.setState(CreatureState.FLIGHT_TELEPORT);
		player.unsetState(CreatureState.ACTIVE);
		player.setFlightTeleportId(id);
		PacketSendUtility.sendPacket(player, new S_ACTION(player, EmotionType.START_FLYTELEPORT, id, 0));
	}
	
	@Override
	public boolean onMovieEndEvent(final QuestEnv env, int movieId) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (movieId == 155) {
			changeQuestStep(env, 94, 98, false);
			QuestService.addNewSpawn(310070000, player.getInstanceId(), 205111, player.getX(), player.getY(), player.getZ(), (byte) 0);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onKillEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			int var1 = qs.getQuestVarById(1);
			if (var == 97) {
				if (var1 >= 0 && var1 < 0) {
					return defaultOnKillEvent(env, LF1B_SpectreQ_21_Ae, var1, var1 + 1, 1);
				} else if (var1 == 0) {
					removeStigma(env);
					final QuestEnv qe = env;
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							changeQuestStep(qe, 97, 8, false);
							TeleportService2.teleportTo(env.getPlayer(), 210030000, 2315.0000f, 1800.0000f, 195.0000f, (byte) 0);
						}
					}, 3000);
					return true;
				}
			}
		}
		return false;
	}
	
	private int getStoneId(Player player) {
		switch (player.getCommonData().getPlayerClass()) {
			case GLADIATOR: {
				return 140000001; //Healing Light II
			} case TEMPLAR: {
				return 140000001; //Healing Light II
			} case ASSASSIN: {
				return 140000001; //Healing Light II
			} case RANGER: {
				return 140000001; //Healing Light II
			} case SORCERER: {
				return 140000001; //Healing Light II
			} case SPIRIT_MASTER: {
				return 140000001; //Healing Light II
			} case CLERIC: {
				return 140000001; //Healing Light II
			} case CHANTER: {
				return 140000001; //Healing Light II
			} default: {
				return 0;
			}
		}
	}
	
	private boolean isStigmaEquipped(final QuestEnv env) {
		final Player player = env.getPlayer();
		for (Item i: player.getEquipment().getEquippedItemsAllStigma()) {
			if (i.getItemId() == getStoneId(player)) {
				return true;
			}
		}
		return false;
	}
	
	private void removeStigma(final QuestEnv env) {
		final Player player = env.getPlayer();
		for (Item item: player.getEquipment().getEquippedItemsByItemId(getStoneId(player))) {
			player.getEquipment().unEquipItem(item.getObjectId(), 0);
		}
		removeQuestItem(env, getStoneId(player), 1);
	}
	
	@Override
    public boolean onLogOutEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            removeStigma(env);
			int var = qs.getQuestVarById(0);
            if (var >= 93 && var < 98) {
				qs.setQuestVar(2);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
	
	@Override
	public boolean onDieEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			removeStigma(env);
			int var = qs.getQuestVars().getQuestVars();
			if (var >= 93 && var < 98) {
				qs.setQuestVar(2);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}
}