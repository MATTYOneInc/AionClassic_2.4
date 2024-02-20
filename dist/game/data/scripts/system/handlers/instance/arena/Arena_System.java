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
package instance.arena;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.controllers.attack.AggroInfo;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.actions.PlayerActions;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.instancereward.InstanceReward;
import com.aionemu.gameserver.model.instance.instancereward.PvPArenaReward;
import com.aionemu.gameserver.model.instance.playerreward.PvPArenaPlayerReward;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.model.DispelCategoryType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

/****/
/** Author Rinzler (Encom)
/****/

public class Arena_System extends GeneralInstanceHandler
{
	protected int killBonus;
	protected int deathFine;
	private boolean isInstanceDestroyed;
	protected PvPArenaReward instanceReward;
	
	protected final int SKILL_Q_SPEEDUP = 1856;
	
	@Override
	public boolean onDie(Player player, Creature lastAttacker) {
		PvPArenaPlayerReward ownerReward = getPlayerReward(player.getObjectId());
		ownerReward.endBoostMoraleEffect(player);
		ownerReward.applyBoostMoraleEffect(player);
		sendPacket();
		PacketSendUtility.broadcastPacket(player, new S_ACTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
		PacketSendUtility.sendPacket(player, new S_RESURRECT_INFO(false, false, 0, 8));
		if (lastAttacker != null && lastAttacker != player) {
			if (lastAttacker instanceof Player) {
				Player winner = (Player) lastAttacker;
				PvPArenaPlayerReward reward = getPlayerReward(winner.getObjectId());
				reward.addPvPKillToPlayer();
				int worldId = winner.getWorldId();
				QuestEngine.getInstance().onKillInWorld(new QuestEnv(player, winner, 0, 0), worldId);
				switch (winner.getRace()) {
				    case ELYOS:
						final QuestState qs18212 = winner.getQuestStateList().getQuestState(18212); //First Blood.
						if (qs18212 != null && qs18212.getStatus() == QuestStatus.START && qs18212.getQuestVarById(0) == 0) {
							ItemService.addItem(player, 182212218, 1);
							ClassChangeService.onUpdateQuest18212(player);
						}
					break;
					case ASMODIANS:
						final QuestState qs28212 = winner.getQuestStateList().getQuestState(28212); //A Test Of Blood.
						if (qs28212 != null && qs28212.getStatus() == QuestStatus.START && qs28212.getQuestVarById(0) == 0) {
							ItemService.addItem(player, 182212221, 1);
							ClassChangeService.onUpdateQuest28212(player);
						}
					break;
				}
			}
		}
		updatePoints(player);
		return true;
	}
	
	private void updatePoints(Creature victim) {
		if (!instanceReward.isStartProgress()) {
			return;
		}
		int bonus = 0;
		int rank = 0;
		if (victim instanceof Player) {
			PvPArenaPlayerReward victimFine = getPlayerReward(victim.getObjectId());
			victimFine.addPoints(deathFine);
			bonus = killBonus;
			rank = instanceReward.getRank(victimFine.getPoints());
		} else {
			bonus = getNpcBonus(((Npc) victim).getNpcId());
		} if (bonus == 0) {
			return;
		} for (AggroInfo damager : victim.getAggroList().getList()) {
			if (!(damager.getAttacker() instanceof Creature)) {
				continue;
			}
			Creature master = ((Creature) damager.getAttacker()).getMaster();
			if (master == null) {
				continue;
			} if (master instanceof Player) {
				Player attaker = (Player) master;
				int rewardPoints = (victim instanceof Player && instanceReward.getRound() == 3 && rank == 0 ? bonus * 3 : bonus) * damager.getDamage() / victim.getAggroList().getTotalDamage();
				getPlayerReward(attaker.getObjectId()).addPoints(rewardPoints);
				sendSystemMsg(attaker, victim, rewardPoints);
			}
		} if (instanceReward.hasCapPoints()) {
			instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
			reward();
		}
		sendPacket();
	}
	
	protected void sendSystemMsg(Player player, Creature creature, int rewardPoints) {
		int nameId = creature.getObjectTemplate().getNameId();
		DescriptionId name = new DescriptionId(nameId * 2 + 1);
		PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400237, nameId == 0 ? creature.getName() : name, rewardPoints));
	}
	
	@Override
	public void onDie(Npc npc) {
		if (npc.getAggroList().getMostPlayerDamage() == null) {
			return;
		}
		updatePoints(npc);
		final int npcId = npc.getNpcId();
		//Blessed Relics.
		if (npcId == 701173 || npcId == 701187) {
			spawnBlessedRelics(30000);
		}
		//Cursed Relics.
		if (npcId == 701174 || npcId == 701188) {
			spawnCursedRelics(30000);
		}
	}
	
	@Override
	public void onEnterInstance(final Player player) {
		Integer object = player.getObjectId();
		if (!containPlayer(object)) {
			instanceReward.regPlayerReward(object);
			getPlayerReward(object).applyBoostMoraleEffect(player);
			instanceReward.setRndPosition(object);
		} else {
			instanceReward.portToPosition(player);
		}
		sendPacket();
	}
	
	private void sendPacket(final AionServerPacket packet) {
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, packet);
			}
		});
	}
	
	private void spawnBlessedRelics(int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
					spawn(Rnd.get(1, 2) == 1 ? 701173 : 701187, 1841.951f, 1733.968f, 300.242f, (byte) 0);
				}
			}
		}, time);
	}
	
	private void spawnCursedRelics(int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
					spawn(Rnd.get(1, 2) == 1 ? 701174 : 701188, 674.517f, 1778.428f, 204.693f, (byte) 0);
				}
			}
		}, time);
	}
	
	private int getNpcBonus(int npcId) {
		switch (npcId) {
			case 218684:
			case 218709: //Black Claw Scratcher.
			case 218710: //Mutated Drakan Fighter.
			case 218718: //Red Sand Brax.
			case 218719: //Red Sand Tog.
			    return 100;
			case 218711: //Casus Manor Guard.
			case 218712: //Casus Manor Maidservant.
			case 218713: //Casus Manor Maid.
				return 250;
			case 218806: //Casus Manor Chief Maid.
			case 218968: //Blacksand Brax.
			case 218971: //Blacksand Tog.
			    return 500;
			case 218714: //Casus Manor Butler.
			    return 650;
			case 218715: //Casus Manor Noble.
			    return 750;
			case 218708: //Mumu Rake Gatherer.	
				return 1250;
			case 218716: //Pale Carmina.
			case 218717: //Corrupt Casus.
				return 1500;
			default:
				return 0;
		}
	}
	
	@Override
	public InstanceReward<?> getInstanceReward() {
		return instanceReward;
	}
	
	@Override
	public void onPlayerLogOut(Player player) {
		TeleportService2.moveToBindLocation(player, true);
		getPlayerReward(player.getObjectId()).updateLogOutTime();
	}
	
	@Override
	public void onPlayerLogin(Player player) {
		getPlayerReward(player.getObjectId()).updateBonusTime();
	}
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		instanceReward = new PvPArenaReward(mapId, instanceId, instance);
		instanceReward.setInstanceScoreType(InstanceScoreType.PREPARING);
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
				player.getController().stopProtectionActiveTask();
			}
		});
		///Arena Of Discipline.
		///Red Sand Brax + Red Sand Tog.
		switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218718, 1838.8525f, 1052.9792f, 337.6463f, (byte) 2);
				spawn(218719, 1854.5613f, 1071.1096f, 337.27164f, (byte) 94);
				spawn(218718, 1872.4758f, 1053.5889f, 337.54938f, (byte) 60);
				spawn(218719, 1856.8169f, 1037.302f, 337.59766f, (byte) 30);
			break;
			case 2:
				spawn(218719, 1838.8525f, 1052.9792f, 337.6463f, (byte) 2);
				spawn(218718, 1854.5613f, 1071.1096f, 337.27164f, (byte) 94);
				spawn(218719, 1872.4758f, 1053.5889f, 337.54938f, (byte) 60);
				spawn(218718, 1856.8169f, 1037.302f, 337.59766f, (byte) 30);
			break;
		}
		///Arena Of Glory.
		///Blacksand Brax + Blacksand Tog.
		switch (Rnd.get(1, 2)) {
			case 1:
				spawn(218968, 1842.5076f, 1140.0535f, 337.00397f, (byte) 2);
				spawn(218971, 1858.2319f, 1158.2822f, 337.67612f, (byte) 89);
				spawn(218968, 1876.0700f, 1140.6621f, 337.43823f, (byte) 61);
				spawn(218971, 1860.4895f, 1124.4128f, 337.37097f, (byte) 31);
			break;
			case 2:
				spawn(218971, 1842.5076f, 1140.0535f, 337.00397f, (byte) 2);
				spawn(218968, 1858.2319f, 1158.2822f, 337.67612f, (byte) 89);
				spawn(218971, 1876.0700f, 1140.6621f, 337.43823f, (byte) 61);
				spawn(218968, 1860.4895f, 1124.4128f, 337.37097f, (byte) 31);
			break;
		} if (!instanceReward.isSoloArena()) {
			spawnCursedRelics(0);
			spawnBlessedRelics(0);
		}
		instanceReward.setInstanceStartTime();
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
					openDoors();
					///The member recruitment window has passed. You cannot recruit any more members.
					sendMsgByRace(1401181, Race.PC_ALL, 5000);
					instanceReward.setInstanceScoreType(InstanceScoreType.START_PROGRESS);
					sendPacket();
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
								instanceReward.setRound(2);
								instanceReward.setRndZone();
								sendPacket();
								changeZone();
								///Defeating a higher rank player in this round will result in additional points.
								sendMsgByRace(1401203, Race.PC_ALL, 2000);
								ThreadPoolManager.getInstance().schedule(new Runnable() {
									@Override
									public void run() {
										if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
											instanceReward.setRound(3);
											instanceReward.setRndZone();
											sendPacket();
											changeZone();
											///Defeating a higher rank player in this round will result in additional points.
											sendMsgByRace(1401203, Race.PC_ALL, 2000);
											ThreadPoolManager.getInstance().schedule(new Runnable() {
												@Override
												public void run() {
													if (!isInstanceDestroyed && !instanceReward.isRewarded()) {
                                                        instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
                                                        reward();
														sendPacket();
                                                    }
                                                }
                                            }, 180000);
                                        }
                                    }
                                }, 180000);
                            }
                        }
                    }, 180000);
                }
            }
        }, 120000);
    }
	
	protected void sendMsgByRace(final int msg, final Race race, int time) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.getRace().equals(race) || race.equals(Race.PC_ALL)) {
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(msg));
						}
					}
				});
			}
		}, time);
	}
	
	@Override
	public void onExitInstance(Player player) {
		TeleportService2.moveToBindLocation(player, true);
	}
	
	private void openDoors() {
		for (StaticDoor door : instance.getDoors().values()) {
			if (door != null) {
				door.setOpen(true);
			}
		}
	}
	
	private boolean containPlayer(Integer object) {
		return instanceReward.containPlayer(object);
	}
	
	protected PvPArenaPlayerReward getPlayerReward(Integer object) {
		instanceReward.regPlayerReward(object);
		return (PvPArenaPlayerReward) instanceReward.getPlayerReward(object);
	}
	
	@Override
    public boolean onReviveEvent(Player player) {
		player.getGameStats().updateStatsAndSpeedVisually();
		PlayerReviveService.revive(player, 100, 100, false, 0);
		PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_REBIRTH_MASSAGE_ME);
		PacketSendUtility.sendPacket(player, new S_ASK(S_ASK.STR_INSTANT_DUNGEON_RESURRECT, 0, 0));
		instanceReward.portToPosition(player);
		return true;
    }
	
	@Override
	public void onLeaveInstance(Player player) {
		PvPArenaPlayerReward playerReward = getPlayerReward(player.getObjectId());
		if (playerReward != null) {
			playerReward.endBoostMoraleEffect(player);
			instanceReward.clearPosition(playerReward.getPosition(), Boolean.FALSE);
			instanceReward.removePlayerReward(playerReward);
		}
		//"Player Name" has left the battle.
		PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400255, player.getName()));
	}
	
	protected void sendPacket() {
		instanceReward.sendPacket();
	}
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
	
	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
		instanceReward.clear();
	}
	
	private void changeZone() {
		for (Player player: instance.getPlayersInside()) {
			instanceReward.portToPosition(player);
		}
		sendPacket();
	}
	
	protected void reward() {
		for (Player player : instance.getPlayersInside()) {
			if (PlayerActions.isAlreadyDead(player)) {
				PlayerReviveService.duelRevive(player);
			}
			PvPArenaPlayerReward reward = getPlayerReward(player.getObjectId());
			if (!reward.isRewarded()) {
				reward.setRewarded();
				AbyssPointsService.addAp(player, reward.getBasicAP() + reward.getRankingAP() + reward.getScoreAP());
				int courage = reward.getScoreCourage();
				if (courage != 0) {
					ItemService.addItem(player, 186000170, 31);
				}
				int crucible = reward.getScoreCrucible();
				if (crucible != 0) {
					ItemService.addItem(player, 186000169, 123);
				}
			}
		} for (Npc npc: instance.getNpcs()) {
			npc.getController().onDelete();
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isInstanceDestroyed) {
					for (Player player : instance.getPlayersInside()) {
						onExitInstance(player);
					}
					AutoGroupService.getInstance().unRegisterInstance(instanceId);
				}
			}
		}, 10000);
	}
	
	@Override
   	public void checkDistance(final Player player, final Npc npc) {
		switch(npc.getNpcId()) {
			case 701183: //idarena_pvp01_s2_ring01_61.
			case 701184: //idarena_pvp01_s2_ring02_61.
			    if (MathUtil.isIn3dRange(npc, player, 10)) {
					if (!player.getEffectController().hasAbnormalEffect(SKILL_Q_SPEEDUP)) {
						SkillEngine.getInstance().applyEffectDirectly(SKILL_Q_SPEEDUP, player, player, 5000 * 1);
					}
				}
			break;
		}
    }
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch(npc.getNpcId()) {
			case 701169: //Plaza Flame Thrower.
			    despawnNpc(npc);
			break;
			case 701170: //Plaza Flame Thrower.
			    despawnNpc(npc);
			break;
			case 701171: //Plaza Flame Thrower.
			    despawnNpc(npc);
			break;
			case 701172: //Plaza Flame Thrower.
			    despawnNpc(npc);
			break;
			case 207102: //Recovery Relics.
			case 701216: //Recovery Relics.
			case 701225: //Recovery Relics.
			case 701226: //Recovery Relics.
			case 701317: //Recovery Relics.
				sendPacket();
				despawnNpc(npc);
				sendSystemMsg(player, npc, 400);
				getPlayerReward(player.getObjectId()).addPoints(400);
				SkillEngine.getInstance().applyEffectDirectly(20053, player, player, 30000 * 1); //Blessing Aura.
			break;
			case 701185: //Empty Anti Aircraft Gun.
			    if (player.isTransformed()) {
					//You cannot use this skill while transformed.
				    sendMsgByRace(1300149, Race.PC_ALL, 0);
				} else {
					despawnNpc(npc);
				    SkillEngine.getInstance().applyEffectDirectly(20048, player, player, 30000 * 1); //Board Antiaircraft Gun.
				}
			break;
			//Blessed Relics.
			case 701173:
			case 701187:
			    sendPacket();
			    despawnNpc(npc);
				sendSystemMsg(player, npc, 1750);
			    getPlayerReward(player.getObjectId()).addPoints(1750);
			break;
			//Cursed Relics.
			case 701174:
			case 701188:
			    sendPacket();
			    despawnNpc(npc);
				sendSystemMsg(player, npc, -1750);
			    getPlayerReward(player.getObjectId()).addPoints(-1750);
			break;
			
		   /**
			* Treasure Box
			* [Arena Of Chaos/Chaos Training Grounds]
			*/
			case 218784:
			case 218785:
			case 218786:
			case 218787:
			case 218788:
			case 218789:
			case 218790:
		   /**
			* Treasure Box
			* [Arena Of Discipline/Discipline Training Grounds]
			*/
			case 218791:
			case 218792:
			case 218793:
			case 218794:
			case 218795:
			case 218799:
			case 218800:
			case 218812:
			case 218814:
				switch (Rnd.get(1, 3)) {
					case 1:
					    ItemService.addItem(player, 186000169, 5);
					break;
					case 2:
					    ItemService.addItem(player, 186000170, 5);
					break;
					case 3:
					    ItemService.addItem(player, 186000171, 5);
					break;
				}
				despawnNpc(npc);
			break;
		} if (!instanceReward.isStartProgress()) {
			return;
		}
		int rewardPoints = getNpcBonus(npc.getNpcId());
		int skill = instanceReward.getNpcBonusSkill(npc.getNpcId());
		if (skill != 0) {
			useSkill(npc, player, skill >> 8, skill & 0xFF);
		}
		getPlayerReward(player.getObjectId()).addPoints(rewardPoints);
		sendSystemMsg(player, npc, rewardPoints);
		sendPacket();
	}
	
	protected void useSkill(Npc npc, Player player, int skillId, int level) {
		SkillEngine.getInstance().getSkill(npc, skillId, level, player).useNoAnimationSkill();
	}
}