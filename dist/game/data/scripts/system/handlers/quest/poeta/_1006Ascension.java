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

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.serverpackets.S_PLAY_MODE;
import com.aionemu.gameserver.network.aion.serverpackets.S_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.questEngine.handlers.HandlerResult;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestDialog;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.zone.ZoneName;

import java.util.ArrayList;
import java.util.List;

/****/
/** Author Rinzler (Encom)
/****/

public class _1006Ascension extends QuestHandler
{
	private final static int questId = 1006;
	private final static int[] npcs = {205000, 730008, 790001};
	private final static int[] grunt = {211042, 211043};
	
	public _1006Ascension() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int npc: npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		} for (int mob: grunt) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} if (CustomConfig.ENABLE_SIMPLE_2NDCLASS) {
			return;
		}
		qe.registerOnDie(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterWorld(questId);
		qe.registerOnMovieEndQuest(151, questId);
		qe.registerQuestItem(182200007, questId);
		qe.registerQuestNpc(211043).addOnAttackEvent(questId);
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
		int targetId = env.getTargetId();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 790001) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 3) {
							return sendQuestDialog(env, 1693);
						} else if (var == 5) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_1: {
						giveQuestItem(env, 182200007, 1);
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_3: {
						qs.setQuestVar(99);
						updateQuestStatus(env);
						removeQuestItem(env, 182200009, 1);
						WorldMapInstance karamatisA = InstanceService.getNextAvailableInstance(310010000);
						InstanceService.registerPlayerWithInstance(karamatisA, player);
						TeleportService2.teleportTo(player, 310010000, karamatisA.getInstanceId(), 52.0000f, 174.0000f, 229.0000f, (byte) 0);
						return closeDialogWindow(env);
					} case STEP_TO_4: {
						PlayerClass playerClass = player.getCommonData().getPlayerClass();
						if (playerClass == PlayerClass.WARRIOR) {
							return sendQuestDialog(env, 2375);
						} else if (playerClass == PlayerClass.SCOUT) {
							return sendQuestDialog(env, 2716);
						} else if (playerClass == PlayerClass.MAGE) {
							return sendQuestDialog(env, 3057);
						} else if (playerClass == PlayerClass.PRIEST) {
							return sendQuestDialog(env, 3398);
						}
					} case STEP_TO_5: {
						return setPlayerClass(env, qs, PlayerClass.GLADIATOR);
					} case STEP_TO_6: {
						return setPlayerClass(env, qs, PlayerClass.TEMPLAR);
					} case STEP_TO_7: {
						return setPlayerClass(env, qs, PlayerClass.ASSASSIN);
					} case STEP_TO_8: {
						return setPlayerClass(env, qs, PlayerClass.RANGER);
					} case STEP_TO_9: {
						return setPlayerClass(env, qs, PlayerClass.SORCERER);
					} case STEP_TO_10: {
						return setPlayerClass(env, qs, PlayerClass.SPIRIT_MASTER);
					} case STEP_TO_11: {
						return setPlayerClass(env, qs, PlayerClass.CLERIC);
					} case STEP_TO_12: {
						return setPlayerClass(env, qs, PlayerClass.CHANTER);
					}
				}
			} if (targetId == 730008) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1352);
						}
					} case SELECT_ACTION_1353: {
						playQuestMovie(env, 14);
						return sendQuestDialog(env, 1353);
					} case STEP_TO_2: {
						return defaultCloseDialog(env, 2, 3, false, false, 182200009, 1, 182200008, 1);
					}
				}
			} if (targetId == 205000) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (qs.getQuestVars().getQuestVars() == 99) {
							flyTeleport(player, 1001);
							qs.setQuestVar(50);
							updateQuestStatus(env);
							final QuestEnv qe = env;
							final int instanceId = player.getInstanceId();
							SkillEngine.getInstance().applyEffectDirectly(1910, player, player, 900000); //Belpartan's Blessing.
							ThreadPoolManager.getInstance().schedule(new Runnable() {
								@Override
								public void run() {
									changeQuestStep(qe, 50, 51, false);
									List<Npc> drakan = new ArrayList<Npc>();
									drakan.add((Npc) QuestService.addNewSpawn(310010000, instanceId, 211042, (float) 231.71233, (float) 235.16295, (float) 206.49400, (byte) 40));
									drakan.add((Npc) QuestService.addNewSpawn(310010000, instanceId, 211042, (float) 237.76411, (float) 242.27676, (float) 206.45883, (byte) 53));
									drakan.add((Npc) QuestService.addNewSpawn(310010000, instanceId, 211042, (float) 224.95085, (float) 261.26990, (float) 205.41927, (byte) 86));
									drakan.add((Npc) QuestService.addNewSpawn(310010000, instanceId, 211042, (float) 233.77672, (float) 253.57774, (float) 205.73764, (byte) 70));
									for (Npc mob: drakan) {
										mob.getAggroList().addDamage(player, 1000);
									}
								}
							}, 43000);
							return true;
						}
					}
				}
			}
		} else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 790001) {
				return sendQuestEndDialog(env);
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
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (movieId == 151) {
			qs.setQuestVar(5);
			updateQuestStatus(env);
			QuestService.addNewSpawn(310010000, player.getInstanceId(), 790001, (float) 220.5327, (float) 244.6108, (float) 206.39687, (byte) 0);
			return true;
		}
		return false;
	}
	
	@Override
    public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (!player.isInsideZone(ZoneName.get("CLIONA_LAKE_210010000"))) {
				return HandlerResult.UNKNOWN;
			}
            int var = qs.getQuestVarById(0);
            if (var == 1) {
                return HandlerResult.fromBoolean(useQuestItem(env, item, 1, 2, false, 182200008, 1, 0));
            }
        }
        return HandlerResult.FAILED;
    }
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		final int instanceId = player.getInstanceId();
		List<Npc> mobs = new ArrayList<Npc>();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var >= 51 && var < 54) {
				return defaultOnKillEvent(env, 211042, 51, 54);
			} else if (var == 54) {
				qs.setQuestVar(4);
				updateQuestStatus(env);
				Npc mob = (Npc) QuestService.addNewSpawn(310010000, player.getInstanceId(), 211043, (float) 235.78227, (float) 248.14893, (float) 205.71617, (byte) 61);
				mob.getAggroList().addDamage(player, 1000);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean onAttackEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (var == 4) {
				int targetId = 0;
				if (env.getVisibleObject() instanceof Npc) {
					targetId = ((Npc) env.getVisibleObject()).getNpcId();
				} if (targetId == 211043) {
					Npc npc = (Npc) env.getVisibleObject();
					if (npc.getLifeStats().getCurrentHp() < npc.getLifeStats().getMaxHp() / 2) {
						playQuestMovie(env, 151);
						npc.getController().onDelete();
					}
				}
			}
		}
		return false;
	}
	
	private boolean setPlayerClass(QuestEnv env, QuestState qs, PlayerClass playerClass) {
		final Player player = env.getPlayer();
		ClassChangeService.setClass(player, playerClass);
		player.getController().upgradePlayer();
		TeleportService2.teleportTo(env.getPlayer(), 210010000, 245.0000f, 1639.0000f, 100.0000f, (byte) 60);
		return true;
	}
	
	@Override
	public boolean onDieEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() != QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (var == 4 || (var >= 50 && var <= 55)) {
				qs.setQuestVar(3);
				updateQuestStatus(env);
			}
		}
		return false;
	}
	
	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (var == 4 || (var >= 50 && var <= 55) || var == 99) {
				if (player.getWorldId() != 310010000) {
					qs.setQuestVar(3);
					updateQuestStatus(env);
				} else {
					PacketSendUtility.sendPacket(player, new S_PLAY_MODE(1));
					return true;
				}
			} if (var == 5) {
				if (player.getWorldId() == 210010000) {
					qs.setStatus(QuestStatus.REWARD);
					QuestService.finishQuest(env);
					return closeDialogWindow(env);
				}
			}
		}
		return false;
	}
}