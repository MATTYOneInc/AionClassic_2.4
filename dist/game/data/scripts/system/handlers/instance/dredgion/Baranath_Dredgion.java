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
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.actions.PlayerActions;
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
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
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

@InstanceID(300110000)
public class Baranath_Dredgion extends GeneralInstanceHandler
{
	private int bulkhead;
	private long instanceTime;
	private int idab1DreadgionSurkana;
	private Map<Integer, StaticDoor> doors;
	protected DredgionReward dredgionReward;
	private float loosingGroupMultiplier = 1;
	private boolean isInstanceDestroyed = false;
	private final FastList<Future<?>> baranathTask = FastList.newInstance();
	
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
		switch (npcId) {
			case 215087: //idab1_dreadgion_drakanrakeynameda_50_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000040, 1, true));
			break;
			case 215088: //idab1_dreadgion_drakanrakeynamedb_50_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000072, 1, true));
			break;
		}
	}
	
	private void onDieSurkan(Npc npc, Player mostPlayerDamage, int score) {
        Race race = mostPlayerDamage.getRace();
        captureRoom(race, npc.getNpcId() + 14 - 700498); //Captain's Cabin Power Surkana.
        for (Player player: instance.getPlayersInside()) {
			///%0 has destroyed %1.
            PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400199, new DescriptionId(race.equals(Race.ASMODIANS) ? 1800483 : 1800481), new DescriptionId(npc.getObjectTemplate().getNameId() * 2 + 1)));
        }
		idab1DreadgionSurkana++;
		if (idab1DreadgionSurkana == 5) {
            ///Captain Adhati has appeared in the Captain's Cabin.
			sendMsgByRace(1400405, Race.PC_ALL, 0);
			spawn(214823, 485.0000f, 812.0000f, 416.0000f, (byte) 31); //Captain Adhati.
        }
		getPlayerReward(mostPlayerDamage).captureZone();
        updateScore(mostPlayerDamage, npc, score, false);
        npc.getController().onDelete();
    }
	
	protected void startInstanceTask() {
		instanceTime = System.currentTimeMillis();
		baranathTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				openFirstDoors();
				despawnNpcs(instance.getNpcs(206173)); //Invincible Barriere.
				///The bulkhead has been activated and the passage between the First Armory and Gravity Control has been sealed.
				sendMsgByRace(1400595, Race.PC_ALL, 5000);
				///The bulkhead has been activated and the passage between the Second Armory and Gravity Control has been sealed.
				sendMsgByRace(1400596, Race.PC_ALL, 10000);
				///The member recruitment window has passed. You cannot recruit any more members.
				sendMsgByRace(1401181, Race.PC_ALL, 30000);
				dredgionReward.setInstanceScoreType(InstanceScoreType.START_PROGRESS);
				sendPacket();
				///Quartermaster Vujara.
				switch (Rnd.get(1, 2)) {
					case 1:
					    spawn(215391, 415.0000f, 282.0000f, 409.0000f, (byte) 118);
					break;
					case 2:
					    spawn(215391, 556.0000f, 279.0000f, 409.0000f, (byte) 88);
					break;
				}
				///First Mate Aznaya OR Auditor Nirshaka.
				switch (Rnd.get(1, 2)) {
					case 1:
						final Npc idab1_dreadgion_drakanmasboss_50_ah = (Npc) spawn(215086, 460.0000f, 877.0000f, 405.0000f, (byte) 90);
						idab1_dreadgion_drakanmasboss_50_ah.getSpawn().setWalkerId("idab1_dreadgion_path_idab1_drd_17");
						WalkManager.startWalking((NpcAI2) idab1_dreadgion_drakanmasboss_50_ah.getAi2());
					break;
					case 2:
						final Npc idab1_dreadgion_drakanrainspector_50_ah = (Npc) spawn(215390, 460.0000f, 877.0000f, 405.0000f, (byte) 90);
						idab1_dreadgion_drakanrainspector_50_ah.getSpawn().setWalkerId("idab1_dreadgion_path_idab1_drd_17");
						WalkManager.startWalking((NpcAI2) idab1_dreadgion_drakanrainspector_50_ah.getAi2());
					break;
				}
			}
		}, 60000));
		baranathTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				///A Nuclear Control Room Teleporter has been created at the Emergency Exit.
				sendMsgByRace(1400265, Race.PC_ALL, 0);
				spawn(730187, 398.0000f, 160.0000f, 432.0000f, (byte) 0, 10); //Portside Central Teleporter.
				spawn(730188, 571.0000f, 160.0000f, 432.0000f, (byte) 0, 9); //Starboard Central Teleporter. 
			}
		}, 600000));
		baranathTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
    public void handleUseItemFinish(final Player player, final Npc npc) {
        switch (npc.getNpcId()) {
			case 730196: //Exhausted Shugo.
				switch (player.getRace()) {
				    case ELYOS:
						final QuestState qs3711 = player.getQuestStateList().getQuestState(3711); //[Group] To Kill A Captain.
						if (qs3711 != null && qs3711.getStatus() == QuestStatus.START && qs3711.getQuestVarById(0) == 1) {
							ClassChangeService.onUpdateQuest3711(player);
							PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 428));
						} else {
							///You have not acquired this quest.
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
						}
					break;
					case ASMODIANS:
						final QuestState qs4711 = player.getQuestStateList().getQuestState(4711); //[Group] The Dredgion Captain.
						if (qs4711 != null && qs4711.getStatus() == QuestStatus.START && qs4711.getQuestVarById(0) == 1) {
							ClassChangeService.onUpdateQuest4711(player);
							PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 428));
						} else {
							///You have not acquired this quest.
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
						}
					break;
				}
            break;
        }
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
		    case 214805:
		    case 214807:
			case 214809:
                score += 12;
            break;
			case 214813:
			case 214814:
			case 214815:
			case 214816:
			case 214817:
			case 214818:
                score += 18;
            break;
			case 214806:
			case 214808:
			case 214810:
			case 214811:
			case 214812:
			case 214819:
			case 214820:
			case 214821:
			case 214822:
                score += 42;
            break;
			//Captured Scholar.
		    case 798323:
            case 798324:
            case 798325:
			case 798327:
            case 798328:
            case 798329:
                score += 100;
				despawnNpc(npc);
            break;
			case 215082: //Technician Sarpa.
			case 215083: //Navigator Nevikah.
			case 215084: //Assistant Malakun.
			case 215085: //Adjutant Kundhan.
			case 215087: //Sentinel Garkusa.
			case 215088: //Prison Guard Mahnena.
			case 215089: //Air Captain Girana.
			case 215090: //Vice Air Captain Kai.
			case 215091: //Vice Gun Captain Zha.
			case 215092: //Gun Captain Ankrana.
                score += 200;
            break;
			case 215086: //First Mate Aznaya.
                score += 500;
            break;
			//The Surkana.
			case 700485:
			case 700486:
			case 700495:
			case 700496:
				onDieSurkan(npc, mostPlayerDamage, 500);
			break;
			case 700490:
			case 700491:
				onDieSurkan(npc, mostPlayerDamage, 600);
			break;
			case 700494:
			case 700497:
			case 700498:
				onDieSurkan(npc, mostPlayerDamage, 700);
			break;
			case 700492:
			case 700493:
				onDieSurkan(npc, mostPlayerDamage, 800);
			break;
			case 700487:
				onDieSurkan(npc, mostPlayerDamage, 900);
			break;
			case 700488:
			case 700489:
				onDieSurkan(npc, mostPlayerDamage, 1100);
			break;
			case 700503: //Portside Door Of Captain's Cabin.
				///The Port Captain's Cabin Door has been destroyed.
				sendMsgByRace(1400230, Race.PC_ALL, 0);
			break;
			case 700504: //Starboard Door Of Captain's Cabin.
				///The Starboard Captain's Cabin Door has been destroyed.
				sendMsgByRace(1400231, Race.PC_ALL, 0);
			break;
			case 215427: //Supervisor Lakhane.
				score += 1000;
				///A Captain's Cabin Teleport Device that lasts for 3 minutes has been generated at the end of the Atrium.
				sendMsgByRace(1400234, Race.PC_ALL, 0);
				spawn(730197, 484.0000f, 761.0000f, 388.0000f, (byte) 0, 91); //Captain's Cabin Teleport Device.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnNpcs(instance.getNpcs(730197));
					}
				}, 180000);
            break;
			case 700505: //Portside Teleporter Generator.
                despawnNpc(npc);
				///A Portside Central Teleporter has been generated at the Escape Hatch.
				sendMsgByRace(1400228, Race.PC_ALL, 0);
				spawn(730213, 402.0000f, 175.0000f, 432.0000f, (byte) 0, 64); //No.1 Nuclear Control Room Teleporter.
            break;
			case 700506: //Starboard Teleporter Generator.
                despawnNpc(npc);
				///A Starboard Central Teleporter has been generated at the Secondary Escape Hatch.
				sendMsgByRace(1400229, Race.PC_ALL, 0);
				spawn(730214, 567.0000f, 175.0000f, 432.0000f, (byte) 0, 65); //No.2 Nuclear Control Room Teleporter.
            break;
			case 700501: //Portside Defense Shield.
			case 700502: //Starboard Defense Shield.
				despawnNpc(npc);
			break;
			case 700507: //Portside Defense Shield Generator.
				despawnNpc(npc);
				///The Portside Defense Shield has been generated in Ready Room 1.
				sendMsgByRace(1400226, Race.PC_ALL, 0);
				spawn(700501, 448.0000f, 493.0000f, 394.0000f, (byte) 0, 12); //Portside Defense Shield.
			break;
			case 700508: //Starboard Defense Shield Generator.
				despawnNpc(npc);
				///The Starboard Defense Shield has been generated in Ready Room 2.
				sendMsgByRace(1400227, Race.PC_ALL, 0);
				spawn(700502, 520.0000f, 493.0000f, 394.0000f, (byte) 0, 16); //Starboard Defense Shield.
			break;
			case 700598: //Port Bulkhead.
			case 700599: //Starboard Bulkhead.
				bulkhead++;
				despawnNpc(npc);
				if (bulkhead == 2) {
				    switch (Rnd.get(1, 2)) {
					    case 1:
					        spawn(215082, 456.0000f, 319.0000f, 402.0000f, (byte) 28); //Technician Sarpa.
					    break;
					    case 2:
					        spawn(215093, 513.0000f, 319.0000f, 402.0000f, (byte) 4); //Adjutant Kalanadi.
					    break;
				    }
				}
			break;
            case 214823: //Captain Adhati.
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
        openDoor(17);
        openDoor(18);
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
        baranathTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
        baranathTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
        baranathTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
        for (FastList.Node<Future<?>> n = baranathTask.head(), end = baranathTask.tail(); (n = n.getNext()) != end; ) {
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