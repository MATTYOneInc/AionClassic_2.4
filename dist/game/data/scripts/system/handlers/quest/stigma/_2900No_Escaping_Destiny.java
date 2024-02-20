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
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
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

public class _2900No_Escaping_Destiny extends QuestHandler
{
	private final static int questId = 2900;
	
	private final static int[] npcs = {204182, 203550, 790003, 790002, 203546, 204264, 204061};
	private final static int[] stigmaStone = {140000001};
	private final static int[] DC1_Monster_Helion_Q2900 = {204263};
	
	public _2900No_Escaping_Destiny() {
		super(questId);
	}
	
	@Override
	public void register() {
		qe.registerOnDie(questId);
		qe.registerOnLogOut(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnMovieEndQuest(156, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		for (int npc: npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		} for (int stigma: stigmaStone) {
			qe.registerOnEquipItem(stigma, questId);
		} for (int mob: DC1_Monster_Helion_Q2900) {
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
        } if (player.getRace() == Race.ASMODIANS && player.getLevel() >= 20 && player.getLevel() <= 55) {
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
			if (targetId == 204182) {
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
			} if (targetId == 203550) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						} else if (var == 10) {
							return sendQuestDialog(env, 4080);
						}
					} case STEP_TO_2: {
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					} case STEP_TO_10: {
						return defaultCloseDialog(env, 10, 10, true, false);
					}
				}
			} if (targetId == 790003) {
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
			} if (targetId == 790002) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_4: {
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203546) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 4) {
							return sendQuestDialog(env, 2375);
						} else if (var == 9) {
							return sendQuestDialog(env, 3739);
						}
					} case STEP_TO_5: {
						changeQuestStep(env, 4, 95, false);
						WorldMapInstance spaceOfDestiny = InstanceService.getNextAvailableInstance(320070000);
						InstanceService.registerPlayerWithInstance(spaceOfDestiny, player);
						TeleportService2.teleportTo(player, 320070000, spaceOfDestiny.getInstanceId(), 242.0000f, 248.0000f, 125.0000f);
						return closeDialogWindow(env);
					} case STEP_TO_9: {
						changeQuestStep(env, 9, 10, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 204264) {
				switch (env.getDialog()) {
					case USE_OBJECT: {
						if (var == 99 && !isStigmaEquipped(env)) {
							return sendQuestDialog(env, 3057);
						}
					} case START_DIALOG: {
						if (var == 95) {
							return sendQuestDialog(env, 2716);
						} else if (var == 96) {
							return sendQuestDialog(env, 3057);
						} else if (var == 97) {
							return sendQuestDialog(env, 3398);
						}
					} case STEP_TO_6: {
						playQuestMovie(env, 156);
						return closeDialogWindow(env);
					} case SELECT_ACTION_3058: {
						if (var == 96) {
							if (giveQuestItem(env, getStoneId(player), 1) && !isStigmaEquipped(env)) {
								long stigmaShards = player.getInventory().getItemCountByItemId(141000001);
								if (stigmaShards < 300) {
									if (!player.getInventory().isFull()) {
										ItemService.addItem(player, 141000001, 300 - stigmaShards);
										changeQuestStep(env, 96, 99, false);
										return sendQuestDialog(env, 3058);
									} else if (player.getInventory().isFull()) {
										PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_WAREHOUSE_FULL_INVENTORY);
										return false;
									}
								} else {
									changeQuestStep(env, 96, 99, false);
									return sendQuestDialog(env, 3058);
								}
							} else {
								return closeDialogWindow(env);
							}
						} else if (var == 99) {
							return sendQuestDialog(env, 3058);
						}
					} case STEP_TO_7: {
						if (var == 99) {
							PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(env.getVisibleObject().getObjectId(), 1));
							return true;
						}
					} case STEP_TO_8: {
						changeQuestStep(env, 97, 98, false);
						QuestService.addNewSpawn(320070000, player.getInstanceId(), 204263, player.getX(), player.getY(), player.getZ(), (byte) 0);
						return closeDialogWindow(env);
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204061) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
	
	@Override
	public boolean onEquipItemEvent(final QuestEnv env, int itemId) {
		changeQuestStep(env, 99, 97, false);
		return closeDialogWindow(env);
	}
	
	@Override
	public boolean onMovieEndEvent(final QuestEnv env, int movieId) {
		final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (movieId == 156) {
			changeQuestStep(env, 95, 96, false);
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
			if (var == 98) {
				if (var1 >= 0 && var1 < 0) {
					return defaultOnKillEvent(env, DC1_Monster_Helion_Q2900, var1, var1 + 1, 1);
				} else if (var1 == 0) {
					removeStigma(env);
					final QuestEnv qe = env;
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							changeQuestStep(qe, 98, 9, false);
							TeleportService2.teleportTo(env.getPlayer(), 220010000, 1110.0000f, 1719.0000f, 269.0000f, (byte) 116);
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
            if (var >= 95 && var < 99) {
				qs.setQuestVar(4);
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
			if (var >= 95 && var < 99) {
				qs.setQuestVar(4);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}
}