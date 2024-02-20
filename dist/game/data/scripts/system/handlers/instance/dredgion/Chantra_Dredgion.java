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
package instance.dredgion;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.configs.main.GroupConfig;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.actions.PlayerActions;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.instancereward.DredgionReward;
import com.aionemu.gameserver.model.instance.instancereward.InstanceReward;
import com.aionemu.gameserver.model.instance.playerreward.DredgionPlayerReward;
import com.aionemu.gameserver.model.instance.playerreward.InstancePlayerReward;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.AutoGroupService;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.FastList;

import org.apache.commons.lang.mutable.MutableInt;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300210000)
public class Chantra_Dredgion extends GeneralInstanceHandler
{
	private int bulkhead;
	private long instanceTime;
	private int iddreadgion_02_surkana;
	private Map<Integer, StaticDoor> doors;
	protected DredgionReward dredgionReward;
	private float loosingGroupMultiplier = 1;
	private boolean isInstanceDestroyed = false;
	private final FastList<Future<?>> chantraTask = FastList.newInstance();
	
	protected DredgionPlayerReward getPlayerReward(Player player) {
		Integer object = player.getObjectId();
		if (dredgionReward.getPlayerReward(object) == null) {
			addPlayerToReward(player);
		}
		return (DredgionPlayerReward) dredgionReward.getPlayerReward(object);
	}
	
	protected void captureRoom(Race race, int roomId) {
		dredgionReward.getDredgionRoomById(roomId).captureRoom(race);
	}
	
	private void addPlayerToReward(Player player) {
		dredgionReward.addPlayerReward(new DredgionPlayerReward(player.getObjectId()));
	}
	
	private boolean containPlayer(Integer object) {
		return dredgionReward.containPlayer(object);
	}
	
	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		int index = dropItems.size() + 1;
		switch (npcId) {
			case 700948: ///Balaur Weapon https://aioncodex.com/us/quest/3721-4721/
				for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
				        if (player.getRace() == Race.ELYOS) {
							dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 182202193, 1, true));
						} else {
							dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 182205691, 1, true));
						}
				    }
				}
			break;
			case 217037: //iddreadgion_02_drakanaswait_55_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000105, 1, true));
			break;
		}
	}
	
	private void onDieSurkan(Npc npc, Player mostPlayerDamage, int score) {
		Race race = mostPlayerDamage.getRace();
		captureRoom(race, npc.getNpcId() + 14 - 700851); //Captain's Cabin Power Surkana.
		for (Player player: instance.getPlayersInside()) {
			///%0 has destroyed %1.
			PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400199, new DescriptionId(race.equals(Race.ASMODIANS) ? 1800483 : 1800481), new DescriptionId(npc.getObjectTemplate().getNameId() * 2 + 1)));
		}
		iddreadgion_02_surkana++;
		if (iddreadgion_02_surkana == 5) {
            ///Captain Zanata has appeared in the Captain's Cabin.
			sendMsgByRace(1400632, Race.PC_ALL, 0);
			spawn(216886, 485.47916f, 812.4957f, 416.68475f, (byte) 31); //Captain Zanata.
        }
		getPlayerReward(mostPlayerDamage).captureZone();
		updateScore(mostPlayerDamage, npc, score, false);
		npc.getController().onDelete();
	}
	
	protected void startInstanceTask() {
		instanceTime = System.currentTimeMillis();
		chantraTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				openFirstDoors();
				despawnNpcs(instance.getNpcs(206173)); //Invincible Barriere.
				///The bulkhead has been activated and the passage between the First Armory and Gravity Control has been sealed.
				sendMsgByRace(1400604, Race.PC_ALL, 5000);
				///The bulkhead has been activated and the passage between the Second Armory and Gravity Control has been sealed.
				sendMsgByRace(1400605, Race.PC_ALL, 10000);
				///The member recruitment window has passed. You cannot recruit any more members.
				sendMsgByRace(1401181, Race.PC_ALL, 30000);
				dredgionReward.setInstanceScoreType(InstanceScoreType.START_PROGRESS);
				sendPacket();
				///Quartermaster Bhati.
				switch (Rnd.get(1, 2)) {
					case 1:
					    spawn(216888, 421.80853f, 285.32870f, 409.7311f, (byte) 93);
					break;
					case 2:
					    spawn(216888, 550.34326f, 283.11478f, 409.7311f, (byte) 0);
					break;
				}
				///Hookmatan OR Skyguard Parishka.
				switch (Rnd.get(1, 2)) {
					case 1:
					    spawn(216885, 481.78950f, 881.80130f, 405.01877f, (byte) 0); 
					break;
					case 2:
					    final Npc iddreadgion_02_drakanrainspector_55_ah = (Npc) spawn(216887, 484.54420f, 909.91736f, 405.25128f, (byte) 0);
						iddreadgion_02_drakanrainspector_55_ah.getSpawn().setWalkerId("path_iddreadgion_02_drakanrainspector_55_ah_1");
						WalkManager.startWalking((NpcAI2) iddreadgion_02_drakanrainspector_55_ah.getAi2());
					break;
				}
			}
		}, 60000));
		chantraTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				///A teleport device has been activated in the Emergency Exit.
				sendMsgByRace(1401424, Race.PC_ALL, 0);
				spawn(730311, 415.033875f, 174.003876f, 433.940460f, (byte) 0, 34); //Portside Central Teleporter.
				spawn(730312, 554.648438f, 173.535248f, 433.940460f, (byte) 0, 9); //Starboard Central Teleporter.
			}
		}, 600000));
		chantraTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				///Officer Kamanya has appeared in Gravity Control.
				sendMsgByRace(1400633, Race.PC_ALL, 0);
				spawn(216941, 479.95572f, 314.95938f, 403.3556f, (byte) 36); //Officer Kamanya.
			}
		}, 900000));
		chantraTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!dredgionReward.isRewarded()) {
					Race winningRace = dredgionReward.getWinningRaceByScore();
					stopInstance(winningRace);
				}
			}
		}, 3600000));
	}
	
	@Override
	public void onDie(Npc npc) {
		int score = 0;
		Player mostPlayerDamage = npc.getAggroList().getMostPlayerDamage();
        if (mostPlayerDamage == null) {
            return;
        }
		Race race = mostPlayerDamage.getRace();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 216856:
			case 216857:
			case 216858:
			case 216859:
                score += 12;
            break;
			case 216860:
			case 216861:
			case 216862:
			case 216863:
			case 216864:
                score += 18;
            break;
			case 216865:
			case 216866:
			case 216867:
			case 216868:
			case 216869:
                score += 32;
            break;
			case 216870:
			case 216871:
			case 216872:
			case 216873:
			case 216874:
			case 216875: //Shipmate Badala.
			case 216876: //Horizonist Anuta.
			case 216877: //First Mate Rukana.
			case 216878: //Skylord Vundar.
			case 216879: //First Mate Dubakar.
			case 216880: //Chief Daraka.
			case 216881: //Trigger.
			case 216883: //Quartermaster Nupakun.
			case 216884: //Takahan.
			case 217037: //Gatekeeper Sarta.
                score += 42;
            break;
		    case 700836:
                score += 100;
				despawnNpc(npc);
            break;
			case 216885: //Hookmatan.
			    score += 500;
            break;
			case 216941: //Officier Kamanya.
				score += 1000;
			break;
			//Surkana.
			case 700838:
			case 700839:
				onDieSurkan(npc, mostPlayerDamage, 400);
			break;
			case 700840:
			case 700848:
			case 700849:
			case 700850:
			case 700851:
				onDieSurkan(npc, mostPlayerDamage, 700);
			break;
			case 700845:
			case 700846:
				onDieSurkan(npc, mostPlayerDamage, 800);
			break;
			case 700847:
				onDieSurkan(npc, mostPlayerDamage, 900);
			break;
			case 700841:
			case 700842:
				onDieSurkan(npc, mostPlayerDamage, 1000);
			break;
			case 700843:
			case 700844:
				onDieSurkan(npc, mostPlayerDamage, 1100);
			break;
			case 216882: //Sahadena The Abettor.
				score += 42;
				if (race.equals(Race.ELYOS)) {
				   ///Captain's Cabin teleport device has been created at the end of the Atrium.
				   sendMsgByRace(1400652, Race.ELYOS, 0);
				   spawn(730357, 473.758545f, 761.864258f, 390.805359f, (byte) 0, 33); //Elyos Captain's Cabin Teleporter.
				} else if (race.equals(Race.ASMODIANS)) {
				   ///Captain's Cabin teleport device has been created at the end of the Atrium.
				   sendMsgByRace(1400652, Race.ASMODIANS, 0);
				   spawn(730358, 496.177948f, 761.770142f, 390.805359f, (byte) 0, 186); //Asmodian Captain's Cabin Teleporter.
				}
            break;
			case 730349: //Portside Teleporter Generator.
                despawnNpc(npc);
				///Supplies Storage teleport device has been created at Escape Hatch.
				sendMsgByRace(1400631, Race.PC_ALL, 0);
				spawn(730314, 396.979340f, 184.391739f, 433.940460f, (byte) 0, 42); //Port Supply Room Teleporter.
            break;
			case 730350: //Starboard Teleporter Generator.
                despawnNpc(npc);
				///Supplies Storage teleport device has been created at the Secondary Escape Hatch.
				sendMsgByRace(1400641, Race.PC_ALL, 0);
				spawn(730315, 572.038208f, 185.252136f, 433.940460f, (byte) 0, 10); //Starboard Supply Room Teleporter.
            break;
			case 730345: //Portside Defense Shield.
			case 730346: //Starboard Defense Shield.
				despawnNpc(npc);
			break;
			case 730351: //Portside Defense Shield Generator.
				despawnNpc(npc);
				///The Portside Defense Shield has been generated in Ready Room 1.
				sendMsgByRace(1400226, Race.PC_ALL, 0);
				spawn(730345, 446.728699f, 493.223785f, 395.937500f, (byte) 0, 12); //Portside Defense Shield.
			break;
			case 730352: //Starboard Defense Shield Generator.
				despawnNpc(npc);
				///The Starboard Defense Shield has been generated in Ready Room 2.
				sendMsgByRace(1400227, Race.PC_ALL, 0);
				spawn(730346, 520.403809f, 493.260956f, 395.937500f, (byte) 0, 133); //Starboard Defense Shield.
			break;
			case 730353: //Port Bulkhead.
			case 730354: //Starboard Bulkhead.
				bulkhead++;
				despawnNpc(npc);
				if (bulkhead == 2) {
					///Rajaya The Inquisitor OR Shipmate Badala.
					switch (Rnd.get(1, 2)) {
					    case 1:
					        spawn(216889, 484.86514f, 296.23980f, 402.16040f, (byte) 30);
					    break;
					    case 2:
					        spawn(216875, 484.86514f, 296.23980f, 402.16040f, (byte) 30);
					    break;
				    }
				}
			break;
			case 216886: //Captain Zanata.
				score += 1000;
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				    @Override
					public void run() {
						if (!dredgionReward.isRewarded()) {
							Race winningRace = dredgionReward.getWinningRaceByScore();
							stopInstance(winningRace);
						}
					}
				}, 10000);
			break;
		}
		updateScore(mostPlayerDamage, npc, score, false);
	}
	
	@Override
   	public void checkDistance(final Player player, final Npc npc) {
		switch(npc.getNpcId()) {
			case 206173:
				if (MathUtil.isIn3dRange(npc, player, 10)) {
					if (!player.isGM()) {
						if (player.getRace() == Race.ELYOS) {
							moveBack(player, 572.0000f, 163.0000f, 432.0000f, (byte) 40);
						} else {
							moveBack(player, 398.0000f, 161.0000f, 432.0000f, (byte) 22);
						}
					}
				}
			break;
		}
    }
	
	protected void moveBack(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
	
	protected void despawnNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			npc.getController().onDelete();
		}
	}
	
	protected void openFirstDoors() {
		openDoor(4);
		openDoor(173);
	}
	
	@Override
	public void onEnterInstance(final Player player) {
		if (!containPlayer(player.getObjectId())) {
			addPlayerToReward(player);
		}
		sendPacket();
	}
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		dredgionReward = new DredgionReward(mapId, instanceId);
		dredgionReward.setInstanceScoreType(InstanceScoreType.PREPARING);
		doors = instance.getDoors();
		startInstanceTask();
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
	}
	
	protected void stopInstance(Race race) {
		stopInstanceTask();
		dredgionReward.setWinningRace(race);
		dredgionReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
		doReward();
		sendPacket();
	}
	
	public void doReward() {
		for (Player player : instance.getPlayersInside()) {
			InstancePlayerReward playerReward = getPlayerReward(player);
			float abyssPoint = playerReward.getPoints();
			if (player.getRace().equals(dredgionReward.getWinningRace())) {
				abyssPoint += dredgionReward.getWinnerPoints();
			} else {
				abyssPoint += dredgionReward.getLooserPoints();
			}
			AbyssPointsService.addAp(player, (int) abyssPoint);
			QuestEnv env = new QuestEnv(null, player, 0, 0);
			QuestEngine.getInstance().onDredgionReward(env);
		} for (Npc npc : instance.getNpcs()) {
			npc.getController().onDelete();
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isInstanceDestroyed) {
					for (Player player : instance.getPlayersInside()) {
						if (PlayerActions.isAlreadyDead(player)) {
							PlayerReviveService.duelRevive(player);
						}
						onExitInstance(player);
					}
					AutoGroupService.getInstance().unRegisterInstance(instanceId);
				}
			}
		}, 120000);
	}
	
	private int getTime() {
		long result = System.currentTimeMillis() - instanceTime;
		if (result < 60000) {
			return (int) (60000 - result);
		} else if (result < 3600000) {
			return (int) (3600000 - (result - 60000));
		}
		return 0;
	}
	
	@Override
	public boolean onReviveEvent(Player player) {
		player.getGameStats().updateStatsAndSpeedVisually();
		PlayerReviveService.revive(player, 100, 100, false, 0);
		PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_REBIRTH_MASSAGE_ME);
		PacketSendUtility.sendPacket(player, new S_ASK(S_ASK.STR_INSTANT_DUNGEON_RESURRECT, 0, 0));
		dredgionReward.portToPosition(player);
		return true;
	}
	
	@Override
	public boolean onDie(Player player, Creature lastAttacker) {
		int points = 60;
		PacketSendUtility.broadcastPacket(player, new S_ACTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
        PacketSendUtility.sendPacket(player, new S_RESURRECT_INFO(player.haveSelfRezEffect(), false, 0, 8));
		if (lastAttacker instanceof Player) {
			if (lastAttacker.getRace() != player.getRace()) {
				InstancePlayerReward playerReward = getPlayerReward(player);
				if (getPointsByRace(lastAttacker.getRace()).compareTo(getPointsByRace(player.getRace())) < 0) {
					points *= loosingGroupMultiplier;
				} else if (loosingGroupMultiplier == 10 || playerReward.getPoints() == 0) {
					points = 0;
				}
			    updateScore((Player) lastAttacker, player, points, true);
			}
		}
		updateScore(player, player, -points, false);
		return true;
	}
	
	private MutableInt getPointsByRace(Race race) {
		return dredgionReward.getPointsByRace(race);
	}
	
	private void addPointsByRace(Race race, int points) {
		dredgionReward.addPointsByRace(race, points);
	}
	
	private void addPointToPlayer(Player player, int points) {
		getPlayerReward(player).addPoints(points);
	}
	
	private void addPvPKillToPlayer(Player player) {
		getPlayerReward(player).addPvPKillToPlayer();
	}
	
	private void addBalaurKillToPlayer(Player player) {
		getPlayerReward(player).addMonsterKillToPlayer();
	}
	
	protected void updateScore(Player player, Creature target, int points, boolean pvpKill) {
		if (points == 0) {
			return;
		}
		addPointsByRace(player.getRace(), points);
		List<Player> playersToGainScore = new ArrayList<Player>();
		if (target != null && player.isInGroup2()) {
			for (Player member : player.getPlayerGroup2().getOnlineMembers()) {
				if (member.getLifeStats().isAlreadyDead()) {
					continue;
				} if (MathUtil.isIn3dRange(member, target, GroupConfig.GROUP_MAX_DISTANCE)) {
					playersToGainScore.add(member);
				}
			}
		} else {
			playersToGainScore.add(player);
		} for (Player playerToGainScore : playersToGainScore) {
			addPointToPlayer(playerToGainScore, points / playersToGainScore.size());
			if (target instanceof Npc) {
				PacketSendUtility.sendPacket(playerToGainScore, new S_MESSAGE_CODE(1400237, new DescriptionId(((Npc) target).getObjectTemplate().getNameId() * 2 + 1), points));
			} else if (target instanceof Player) {
				PacketSendUtility.sendPacket(playerToGainScore, new S_MESSAGE_CODE(1400237, target.getName(), points));
			}
		}
		int pointDifference = getPointsByRace(Race.ASMODIANS).intValue() - (getPointsByRace(Race.ELYOS)).intValue();
		if (pointDifference < 0) {
			pointDifference *= -1;
		} if (pointDifference >= 3000) {
			loosingGroupMultiplier = 10;
		} else if (pointDifference >= 1000) {
			loosingGroupMultiplier = 1.5f;
		} else {
			loosingGroupMultiplier = 1;
		} if (pvpKill && points > 0) {
			addPvPKillToPlayer(player);
		} else if (target instanceof Npc && ((Npc) target).getRace().equals(Race.DRAKAN)) {
			addBalaurKillToPlayer(player);
		}
		sendPacket();
	}
	
	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
		dredgionReward.clear();
		stopInstanceTask();
		doors.clear();
	}
	
	protected void openDoor(int doorId) {
		StaticDoor door = doors.get(doorId);
		if (door != null) {
			door.setOpen(true);
		}
	}
	
	private void sendPacket() {
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new S_INSTANT_DUNGEON_INFO(getTime(), dredgionReward, instance.getPlayersInside()));
			}
		});
	}
	
	protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time) {
        sp(npcId, x, y, z, h, 0, time, 0, null);
    }
	
    protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time, final int msg, final Race race) {
        sp(npcId, x, y, z, h, 0, time, msg, race);
    }
	
    protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int entityId, final int time, final int msg, final Race race) {
        chantraTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (!isInstanceDestroyed) {
                    spawn(npcId, x, y, z, h, entityId);
                    if (msg > 0) {
                        sendMsgByRace(msg, race, 0);
                    }
                }
            }
        }, time));
    }
	
    protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time, final String walkerId) {
        chantraTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (!isInstanceDestroyed) {
                    Npc npc = (Npc) spawn(npcId, x, y, z, h);
                    npc.getSpawn().setWalkerId(walkerId);
                    WalkManager.startWalking((NpcAI2) npc.getAi2());
                }
            }
        }, time));
    }
	
    protected void sendMsgByRace(final int msg, final Race race, int time) {
        chantraTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
        }, time));
    }
	
	private void sendMsg(final String str) {
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendWhiteMessageOnCenter(player, str);
			}
		});
	}
	
	private void stopInstanceTask() {
        for (FastList.Node<Future<?>> n = chantraTask.head(), end = chantraTask.tail(); (n = n.getNext()) != end; ) {
            if (n.getValue() != null) {
                n.getValue().cancel(true);
            }
        }
    }
	
	@Override
	public InstanceReward<?> getInstanceReward() {
		return dredgionReward;
	}
	
	@Override
    public void onExitInstance(Player player) {
        TeleportService2.moveToBindLocation(player, true);
    }
	
	@Override
	public void onPlayerLogOut(Player player) {
		TeleportService2.moveToBindLocation(player, true);
	}
	
	@Override
    public void onLeaveInstance(Player player) {
        stopInstanceTask();
		//"Player Name" has left the battle.
		PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400255, player.getName()));
        if (player.isInGroup2()) {
            PlayerGroupService.removePlayer(player);
        }
    }
}