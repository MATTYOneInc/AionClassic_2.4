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

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.serverpackets.S_PLAY_MODE;
import com.aionemu.gameserver.network.aion.serverpackets.S_NPC_HTML_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.S_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
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

import java.util.ArrayList;
import java.util.List;

/****/
/** Author Rinzler (Encom)
/****/

public class _2008Ascension extends QuestHandler
{
	private final static int questId = 2008;
	private final static int[] npcs = {203546, 203550, 205020, 790002, 790003};
	private final static int[] assassin = {205040};
	
	public _2008Ascension() {
		super(questId);
	}
	
	@Override
	public void register() {
		for (int npc: npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        } for (int mob: assassin) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		} if (CustomConfig.ENABLE_SIMPLE_2NDCLASS) {
			return;
		}
		qe.registerOnDie(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterWorld(questId);
		qe.registerOnMovieEndQuest(152, questId);
		qe.registerQuestNpc(205041).addOnAttackEvent(questId);
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
			if (targetId == 203550) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						} else if (var == 4) {
							return sendQuestDialog(env, 2375);
						} else if (var == 6) {
							return sendQuestDialog(env, 2716);
						}
					} case SELECT_ACTION_2376: {
						if (var == 4) {
							playQuestMovie(env, 57);
							removeQuestItem(env, 182203009, 1);
							removeQuestItem(env, 182203010, 1);
							removeQuestItem(env, 182203011, 1);
							return false;
						}
					} case STEP_TO_1: {
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					} case STEP_TO_5: {
						qs.setQuestVar(99);
						updateQuestStatus(env);
						WorldMapInstance karamatisC = InstanceService.getNextAvailableInstance(320020000);
						InstanceService.registerPlayerWithInstance(karamatisC, player);
						TeleportService2.teleportTo(player, 320020000, karamatisC.getInstanceId(), 457.0000f, 426.0000f, 230.0000f, (byte) 0);
						return closeDialogWindow(env);
					} case STEP_TO_6: {
						if (var == 6) {
							PlayerClass playerClass = player.getCommonData().getPlayerClass();
							if (playerClass == PlayerClass.WARRIOR) {
								return sendQuestDialog(env, 3057);
							} else if (playerClass == PlayerClass.SCOUT) {
								return sendQuestDialog(env, 3398);
							} else if (playerClass == PlayerClass.MAGE) {
								return sendQuestDialog(env, 3739);
							} else if (playerClass == PlayerClass.PRIEST) {
								return sendQuestDialog(env, 4080);
							}
						}
					} case STEP_TO_7: {
						if (var == 6) {
							return setPlayerClass(env, qs, PlayerClass.GLADIATOR);
						}
					} case STEP_TO_8: {
						if (var == 6) {
							return setPlayerClass(env, qs, PlayerClass.TEMPLAR);
						}
					} case STEP_TO_9: {
						if (var == 6) {
							return setPlayerClass(env, qs, PlayerClass.ASSASSIN);
						}
					} case STEP_TO_10: {
						if (var == 6) {
							return setPlayerClass(env, qs, PlayerClass.RANGER);
						}
					} case STEP_TO_11: {
						if (var == 6) {
							return setPlayerClass(env, qs, PlayerClass.SORCERER);
						}
					} case STEP_TO_12: {
						if (var == 6) {
							return setPlayerClass(env, qs, PlayerClass.SPIRIT_MASTER);
						}
					} case STEP_TO_13: {
						if (var == 6) {
							return setPlayerClass(env, qs, PlayerClass.CHANTER);
						}
					} case STEP_TO_14: {
						if (var == 6) {
							return setPlayerClass(env, qs, PlayerClass.CLERIC);
						}
					}
				}
			} if (targetId == 790003) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
					} case STEP_TO_2: {
						giveQuestItem(env, 182203009, 1);
						changeQuestStep(env, 1, 2, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 790002) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 2) {
							return sendQuestDialog(env, 1693);
						}
					} case STEP_TO_3: {
						giveQuestItem(env, 182203010, 1);
						changeQuestStep(env, 2, 3, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 203546) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (var == 3) {
							return sendQuestDialog(env, 2034);
						}
					} case STEP_TO_4: {
						giveQuestItem(env, 182203011, 1);
						changeQuestStep(env, 3, 4, false);
						return closeDialogWindow(env);
					}
				}
			} if (targetId == 205020) {
				switch (env.getDialog()) {
					case START_DIALOG: {
						if (qs.getQuestVars().getQuestVars() == 99) {
							flyTeleport(player, 3001);
							qs.setQuestVar(50);
							updateQuestStatus(env);
							final QuestEnv qe = env;
							final int instanceId = player.getInstanceId();
							SkillEngine.getInstance().applyEffectDirectly(1853, player, player, 900000); //Shield Of Hagen.
							ThreadPoolManager.getInstance().schedule(new Runnable() {
								@Override
								public void run() {
									changeQuestStep(qe, 50, 51, false);
									List<Npc> guardianAssassin = new ArrayList<Npc>();
									guardianAssassin.add((Npc) QuestService.addNewSpawn(320020000, instanceId, 205040, 306.56976f, 265.95682f, 205.32211f, (byte) 15));
									guardianAssassin.add((Npc) QuestService.addNewSpawn(320020000, instanceId, 205040, 301.65380f, 270.61760f, 205.84000f, (byte) 5));
									guardianAssassin.add((Npc) QuestService.addNewSpawn(320020000, instanceId, 205040, 300.46542f, 275.21100f, 206.35947f, (byte) 119));
									guardianAssassin.add((Npc) QuestService.addNewSpawn(320020000, instanceId, 205040, 306.11273f, 281.19333f, 206.51040f, (byte) 101));
									for (Npc mob: guardianAssassin) {
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
			if (targetId == 203550) {
				switch (env.getDialog()) {
					case SELECT_NO_REWARD: {
						if (player.getWorldId() == 320020000) {
							qs.setStatus(QuestStatus.REWARD);
							QuestService.finishQuest(env);
							TeleportService2.teleportTo(env.getPlayer(), 220010000, 386.0000f, 1893.0000f, 327.0000f, (byte) 59);
						}
					}
				}
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
		if (movieId != 152) {
			return false;
		}
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START || qs.getQuestVars().getQuestVars() != 5) {
			return false;
		}
		int instanceId = player.getInstanceId();
		QuestService.addNewSpawn(320020000, instanceId, 203550, 313.0000f, 274.0000f, 206.0000f, (byte) 61);
		qs.setQuestVar(6);
		updateQuestStatus(env);
		return true;
	}
	
	@Override
	public boolean onKillEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		int instanceId = player.getInstanceId();
		List<Npc> guardianAssassin = new ArrayList<Npc>();
		if (qs == null || qs.getStatus() != QuestStatus.START) {
			return false;
		}
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();
		if (targetId == 205040) {
			if (var >= 51 && var <= 53) {
				qs.setQuestVar(qs.getQuestVars().getQuestVars() + 1);
				updateQuestStatus(env);
				return true;
			} else if (var == 54) {
				qs.setQuestVar(5);
				updateQuestStatus(env);
				Npc mob = (Npc) QuestService.addNewSpawn(320020000, instanceId, 205041, 295.96387f, 268.81827f, 205.75705f, (byte) 5);
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
		if (qs == null || qs.getStatus() != QuestStatus.START || qs.getQuestVars().getQuestVars() != 5) {
			return false;
		}
		int targetId = env.getTargetId();
		if (targetId != 205041) {
			return false;
		}
		Npc npc = (Npc) env.getVisibleObject();
		if (npc.getLifeStats().getCurrentHp() < npc.getLifeStats().getMaxHp() / 2) {
			playQuestMovie(env, 152);
			npc.getController().onDelete();
		}
		return false;
	}
	
	private boolean setPlayerClass(QuestEnv env, QuestState qs, PlayerClass playerClass) {
		final Player player = env.getPlayer();
		ClassChangeService.setClass(player, playerClass);
		player.getController().upgradePlayer();
		changeQuestStep(env, 6, 6, true);
		return sendQuestDialog(env, 5);
	}
	
	@Override
	public boolean onDieEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() != QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			if (var == 5 || (var == 6 && player.getPlayerClass().isStartingClass()) || (var >= 51 && var <= 53)) {
				qs.setQuestVar(4);
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
			if (var == 5 || (var == 6 && player.getPlayerClass().isStartingClass()) || (var >= 50 && var <= 55) || var == 99) {
				if (player.getWorldId() != 320020000) {
					qs.setQuestVar(4);
					updateQuestStatus(env);
				} else {
					PacketSendUtility.sendPacket(player, new S_PLAY_MODE(1));
					return true;
				}
			}
		}
		return false;
	}
}