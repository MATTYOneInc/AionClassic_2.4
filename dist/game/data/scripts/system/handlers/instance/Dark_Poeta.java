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
package instance;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instance.InstanceScoreType;
import com.aionemu.gameserver.model.instance.instancereward.DarkPoetaReward;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.*;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300040000)
public class Dark_Poeta extends GeneralInstanceHandler
{
	private int rank;
	private long startTime;
	private int powerGenerator;
	private Future<?> timerPrepare;
	private Future<?> timerInstance;
	private boolean isInstanceDestroyed;
	private Map<Integer, StaticDoor> doors;
	private DarkPoetaReward instanceReward;
	//Preparation Time.
	private int prepareTimerSeconds = 7200000; //...2Hrs
	//Duration Instance Time.
	private int instanceTimerSeconds = 7200000; //...2Hrs
	private final FastList<Future<?>> darkPoetaTask = FastList.newInstance();
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			case 700478: //Tahabata Abyss Gate.
				tahabataAbyssGate(1191.0000f, 1246.0000f, 141.0000f, (byte) 76);
			break;
			case 700510: ///Balaur Supply Box https://aioncodex.com/3x/quest/1097-2097/
				switch (player.getRace()) {
				    case ELYOS:
						final QuestState qs1097 = player.getQuestStateList().getQuestState(1097); //Sword Of Transcendence.
						if (qs1097 != null && qs1097.getStatus() == QuestStatus.START && qs1097.getQuestVarById(0) == 2) {
							instance.doOnAllPlayers(new Visitor<Player>() {
								@Override
								public void visit(Player player) {
									if (player.isOnline()) {
										ItemService.addItem(player, 182206060, 1);
									}
								}
							});
						} else {
							///You have not acquired this quest.
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
						}
					break;
					case ASMODIANS:
						final QuestState qs2097 = player.getQuestStateList().getQuestState(2097); //Spirit Blade.
						if (qs2097 != null && qs2097.getStatus() == QuestStatus.START && qs2097.getQuestVarById(0) == 2) {
							instance.doOnAllPlayers(new Visitor<Player>() {
								@Override
								public void visit(Player player) {
									if (player.isOnline()) {
										ItemService.addItem(player, 182207087, 1);
									}
								}
							});
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
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 281088:
			case 281089:
			case 281090:
			case 281091:
			case 281092:
			    score += 3;
			break;
			case 214870:
			    score += 9;
			break;
			case 214831:
			case 214840:
			case 214848:
			case 214856:
			case 215242:
			case 215243:
			case 214860:
			case 214861:
			case 214862:
			case 214863:
			    score += 14;
			break;
			case 214869:
			case 214885:
			    score += 21;
			break;
			case 214877:
			case 214878:
			case 214887:
			case 214888:
			    score += 22;
			break;
			case 214827:
			case 214828:
			case 214829:
			case 214830:
			case 214832:
			case 214833:
			case 214834:
			case 214835:
			case 214844:
			case 214845:
			case 214846:
			case 214847:
			case 214852:
			case 214853:
			case 214854:
			case 214855:
			case 215223:
			case 215224:
			case 215225:
			case 215226:
			case 215227:
			case 215228:
			case 215229:
			case 215230:
			case 215231:
			case 215232:
			case 215233:
			case 215234:
			    score += 26;
			break;
			case 214857:
			case 214858:
			case 214859:
			case 215235:
			case 215236:
			case 215237:
			case 215238:
			case 215239:
			case 215240:
			case 215241:
			case 215246:
			    score += 30;
			break;
			case 214865:
			case 214866:
			case 214867:
			case 214868:
			case 214872:
			case 214873:
			case 214874:
			case 214875:
			case 214881:
			case 214882:
			case 214883:
			case 214884:
			case 214890:
			case 214891:
			case 214892:
			case 214893:
			case 215244:
			case 215245:
			case 215247:
			case 215248:
			case 215249:
			case 215253:
			case 215254:
			case 215255:
			case 215256:
			case 215257:
			case 215258:
			case 215262:
			case 215263:
			case 215264:
			case 215265:
			case 215266:
			case 215267:
			case 215271:
			case 215272:
			case 215273:
			case 215274:
			case 215275:
			case 215276:
			    score += 31;
			break;
			case 700520:
				score += 52;
			break;
			case 214836:
			case 214837:
			case 214838:
			case 214839:
			    score += 76;
			break;
			case 700517:
			case 700518:
			case 700556:
			case 700558:
				score += 157;
			break;
			case 215431:
			case 215432:
				score += 164;
			break;
			case 215433:
			case 215434:
			    score += 173;
			break;
			case 215429:
			case 215430:
			    score += 190;
			break;
			case 214841:
			case 215428:
			    score += 208;
			break;
			case 214842:
			    score += 357;
			break;
			case 215386:
			    score += 409;
			break;
			case 214843:
			    score += 456;
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						NAGABOSS_KILL();
					}
				}, 30000);
			break;
			case 214864:
			case 214880:
			case 215387:
			case 215388:
            case 215389:
			    score += 789;
			break;
			case 214871:
			    score += 1241;
			break;
			case 214849: //Marabata Of Strength.
			    score += 319;
				despawnNpcs(instance.getNpcs(700439)); //Marabata Attack Booster.
				despawnNpcs(instance.getNpcs(700440)); //Marabata Defense Booster.
				despawnNpcs(instance.getNpcs(700441)); //Marabata Property Controller.
			break;
            case 214850: //Marabata Of Aether.
			    score += 319;
			    despawnNpcs(instance.getNpcs(700442)); //Marabata Attack Booster.
				despawnNpcs(instance.getNpcs(700443)); //Marabata Defense Booster.
				despawnNpcs(instance.getNpcs(700444)); //Marabata Property Controller.
			break;
            case 214851: //Marabata Of Poisoning.
			    score += 319;
				despawnNpcs(instance.getNpcs(700445)); //Marabata Attack Booster.
				despawnNpcs(instance.getNpcs(700446)); //Marabata Defense Booster.
				despawnNpcs(instance.getNpcs(700447)); //Marabata Property Controller.
			break;
			case 214895: //Main Power Generator.
			case 214896: //Auxiliary Power Generator.
			case 214897: //Emergency Generator.
			    score += 377;
				powerGenerator++;
				if (powerGenerator == 3) {
					spawn(214904, 275.34537f, 323.02072f, 130.9302f, (byte) 52); //Brigade General Anuhart.
				    instance.doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
							if (player.isOnline()) {
								PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 427));
							}
						}
					});
				}
			break;
			case 219007: //Corrupted Ginseng.
			    score += 2000;
			break;
			///Marabata Of Strength.
			case 700439:
				despawnNpc(npc);
				marabataOfStrength();
				sp(700439, npc.getX(), npc.getY(), npc.getZ(), (byte) 90, 0, 60000, 0, null);
			break;
			case 700440:
				despawnNpc(npc);
				marabataOfStrength();
				sp(700440, npc.getX(), npc.getY(), npc.getZ(), (byte) 13, 0, 60000, 0, null);
			break;
			case 700441:
			    despawnNpc(npc);
				marabataOfStrength();
				sp(700441, npc.getX(), npc.getY(), npc.getZ(), (byte) 49, 0, 60000, 0, null);
			break;
			///Marabata Of Aether.
			case 700442:
			    despawnNpc(npc);
				marabataOfAether();
				sp(700442, npc.getX(), npc.getY(), npc.getZ(), (byte) 49, 0, 60000, 0, null);
			break;
			case 700443:
			    despawnNpc(npc);
				marabataOfAether();
				sp(700443, npc.getX(), npc.getY(), npc.getZ(), (byte) 4, 0, 60000, 0, null);
			break;
			case 700444:
			    despawnNpc(npc);
				marabataOfAether();
				sp(700444, npc.getX(), npc.getY(), npc.getZ(), (byte) 90, 0, 60000, 0, null);
			break;
			///Marabata Of Poisoning.
			case 700445:
			    despawnNpc(npc);
				marabataOfPoisoning();
				sp(700445, npc.getX(), npc.getY(), npc.getZ(), (byte) 14, 0, 60000, 0, null);
			break;
            case 700446:
			    despawnNpc(npc);
				marabataOfPoisoning();
				sp(700446, npc.getX(), npc.getY(), npc.getZ(), (byte) 98, 0, 60000, 0, null);
			break;
            case 700447:
				despawnNpc(npc);
				marabataOfPoisoning();
				sp(700447, npc.getX(), npc.getY(), npc.getZ(), (byte) 59, 0, 60000, 0, null);
			break;
			case 214894: //Telepathy Controller.
			    score += 789;
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						if (player.isOnline()) {
							PacketSendUtility.sendPacket(player, new S_PLAY_CUTSCENE(0, 426));
						}
					}
				});
			break;
			case 215280: //Tahabata Pyrelord.
			case 215281: //Calindi Flamelord.
			case 215282: //Vanuka Infernus.
			case 215283: //Asaratu Bloodshade.
			case 215284: //Chramati Firetail.
			    spawn(730211, 1179.0000f, 1223.0000f, 146.0000f, (byte) 0, 223); //Dark Poeta Exit.
			break;
			case 214904: //Brigade General Anuhart.
			    score += 954;
				spawn(700478, 297.40482f, 316.69537f, 133.12941f, (byte) 56); //Tahabata Abyss Gate.
				if (checkRank(instanceReward.getPoints()) == 1) {
				    //You may only battle Tahabata Pyrelord within the given time limit.
					sendMsgByRace(1400257, Race.PC_ALL, 2000);
					spawn(215280, 1173.3828f, 1224.3948f, 145.1810f, (byte) 21); //Tahabata Pyrelord.
				} if (checkRank(instanceReward.getPoints()) == 2) {
					//Tahabata Pyrelord has left the battle.
					sendMsgByRace(1400258, Race.PC_ALL, 2000);
					//You may only battle Calindi Flamelord within the given time limit.
					sendMsgByRace(1400259, Race.PC_ALL, 4000);
					spawn(215281, 1173.3828f, 1224.3948f, 145.1810f, (byte) 21); //Calindi Flamelord.
				} if (checkRank(instanceReward.getPoints()) == 3) {
					//Calindi Flamelord has left the battle.
					sendMsgByRace(1400260, Race.PC_ALL, 2000);
					spawn(215282, 1173.3828f, 1224.3948f, 145.1810f, (byte) 21); //Vanuka Infernus.
				} if (checkRank(instanceReward.getPoints()) == 4) {
				    spawn(215283, 1173.3828f, 1224.3948f, 145.1810f, (byte) 21); //Asaratu Bloodshade.
				}
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
					    instance.doOnAllPlayers(new Visitor<Player>() {
						    @Override
						    public void visit(Player player) {
							    stopInstance(player);
						    }
					    });
					}
				}, 5000);
			break;
		} if (instanceReward.getInstanceScoreType().isStartProgress()) {
			instanceReward.addNpcKill();
			instanceReward.addPoints(score);
			sendPacket(npc.getObjectTemplate().getNameId(), score);
		}
		PlayerGroup group = instance.getRegisteredGroup();
		if (group != null) {
			if (group.getLeaderObject().getAbyssRank().getRank().getId() >= AbyssRankEnum.STAR1_OFFICER.getId()) {
				score = Math.round(score * 2.0f);
			}
		}
	}
	
	public void NAGABOSS_KILL() {
		//idlf1_b_02_lizardas_50_ae
		final Npc idlf1_b_02_lizardas_50_ae = (Npc) spawn(214837, 807.178162f, 480.325409f, 107.952164f, (byte) 0);
		idlf1_b_02_lizardas_50_ae.getSpawn().setWalkerId("idlf1_b_path_mob_rush_1");
		WalkManager.startWalking((NpcAI2) idlf1_b_02_lizardas_50_ae.getAi2());
		idlf1_b_02_lizardas_50_ae.setState(1);
		PacketSendUtility.broadcastPacket(idlf1_b_02_lizardas_50_ae, new S_ACTION(idlf1_b_02_lizardas_50_ae, EmotionType.START_EMOTE2, 0, idlf1_b_02_lizardas_50_ae.getObjectId()));
		//idlf1_b_02_lizardpr_50_ae
		final Npc idlf1_b_02_lizardpr_50_ae = (Npc) spawn(214839, 806.178162f, 483.325409f, 107.952164f, (byte) 0);
		idlf1_b_02_lizardpr_50_ae.getSpawn().setWalkerId("idlf1_b_path_mob_rush_2");
		WalkManager.startWalking((NpcAI2) idlf1_b_02_lizardpr_50_ae.getAi2());
		idlf1_b_02_lizardpr_50_ae.setState(1);
		PacketSendUtility.broadcastPacket(idlf1_b_02_lizardpr_50_ae, new S_ACTION(idlf1_b_02_lizardpr_50_ae, EmotionType.START_EMOTE2, 0, idlf1_b_02_lizardpr_50_ae.getObjectId()));
		//idlf1_b_lizardfinamed_50_ae
		final Npc idlf1_b_lizardfinamed_50_ae = (Npc) spawn(214841, 807.178162f, 486.325409f, 107.952164f, (byte) 0);
		idlf1_b_lizardfinamed_50_ae.getSpawn().setWalkerId("idlf1_b_path_mob_rush_3");
		WalkManager.startWalking((NpcAI2) idlf1_b_lizardfinamed_50_ae.getAi2());
		idlf1_b_lizardfinamed_50_ae.setState(1);
		PacketSendUtility.broadcastPacket(idlf1_b_lizardfinamed_50_ae, new S_ACTION(idlf1_b_lizardfinamed_50_ae, EmotionType.START_EMOTE2, 0, idlf1_b_lizardfinamed_50_ae.getObjectId()));
		//idlf1_b_02_lizardra_50_ae
		final Npc idlf1_b_02_lizardra_50_ae = (Npc) spawn(214838, 873.178162f, 494.325409f, 112.952164f, (byte) 0);
		idlf1_b_02_lizardra_50_ae.getSpawn().setWalkerId("idlf1_b_path_mob_rush_4");
		WalkManager.startWalking((NpcAI2) idlf1_b_02_lizardra_50_ae.getAi2());
		idlf1_b_02_lizardra_50_ae.setState(1);
		PacketSendUtility.broadcastPacket(idlf1_b_02_lizardra_50_ae, new S_ACTION(idlf1_b_02_lizardra_50_ae, EmotionType.START_EMOTE2, 0, idlf1_b_02_lizardra_50_ae.getObjectId()));
		//idlf1_b_lizardasnamed_50_ae
		final Npc idlf1_b_lizardasnamed_50_ae = (Npc) spawn(214842, 871.178162f, 491.325409f, 112.952164f, (byte) 0);
		idlf1_b_lizardasnamed_50_ae.getSpawn().setWalkerId("idlf1_b_path_mob_rush_5");
		WalkManager.startWalking((NpcAI2) idlf1_b_lizardasnamed_50_ae.getAi2());
		idlf1_b_lizardasnamed_50_ae.setState(1);
		PacketSendUtility.broadcastPacket(idlf1_b_lizardasnamed_50_ae, new S_ACTION(idlf1_b_lizardasnamed_50_ae, EmotionType.START_EMOTE2, 0, idlf1_b_lizardasnamed_50_ae.getObjectId()));
		//idlf1_b_02_lizardfi_50_ae
		final Npc idlf1_b_02_lizardfi_50_ae = (Npc) spawn(214836, 867.178162f, 488.325409f, 111.952164f, (byte) 0);
		idlf1_b_02_lizardfi_50_ae.getSpawn().setWalkerId("idlf1_b_path_mob_rush_6");
		WalkManager.startWalking((NpcAI2) idlf1_b_02_lizardfi_50_ae.getAi2());
		idlf1_b_02_lizardfi_50_ae.setState(1);
		PacketSendUtility.broadcastPacket(idlf1_b_02_lizardfi_50_ae, new S_ACTION(idlf1_b_02_lizardfi_50_ae, EmotionType.START_EMOTE2, 0, idlf1_b_02_lizardfi_50_ae.getObjectId()));
	}
	
	private boolean marabataOfStrength() {
		final Npc booster1 = instance.getNpc(700439);
		final Npc booster2 = instance.getNpc(700440);
		final Npc booster3 = instance.getNpc(700441);
		if (isDead(booster1) && isDead(booster2) && isDead(booster3)) {
			final Npc marabataOfStrength = instance.getNpc(214849);
			if (marabataOfStrength != null) {
				marabataOfStrength.getAi2().think();
				marabataOfStrength.getEffectController().removeEffect(18110); //Torrent Of Wrath.
				marabataOfStrength.getEffectController().removeEffect(18556); //Marabata Barrier.
			}
			return true;
		}
		return false;
	}
	
	private boolean marabataOfAether() {
		final Npc booster4 = instance.getNpc(700442);
		final Npc booster5 = instance.getNpc(700443);
		final Npc booster6 = instance.getNpc(700444);
		if (isDead(booster4) && isDead(booster5) && isDead(booster6)) {
			final Npc marabataOfAether = instance.getNpc(214850);
			if (marabataOfAether != null) {
				marabataOfAether.getAi2().think();
				marabataOfAether.getEffectController().removeEffect(18110); //Torrent Of Wrath.
				marabataOfAether.getEffectController().removeEffect(18556); //Marabata Barrier.
			}
			return true;
		}
		return false;
	}
	
	private boolean marabataOfPoisoning() {
		final Npc booster7 = instance.getNpc(700445);
		final Npc booster8 = instance.getNpc(700446);
		final Npc booster9 = instance.getNpc(700447);
		if (isDead(booster7) && isDead(booster8) && isDead(booster9)) {
			final Npc marabataOfPoisoning = instance.getNpc(214851);
			if (marabataOfPoisoning != null) {
				marabataOfPoisoning.getAi2().think();
				marabataOfPoisoning.getEffectController().removeEffect(18110); //Torrent Of Wrath.
				marabataOfPoisoning.getEffectController().removeEffect(18556); //Marabata Barrier.
			}
			return true;
		}
		return false;
	}
	
	protected void tahabataAbyssGate(float x, float y, float z, byte h) {
		for (Player player: instance.getPlayersInside()) {
			if (player.isOnline()) {
				TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
			}
		}
	}
	
	private int getTime() {
		long result = (int) (System.currentTimeMillis() - startTime);
		return instanceTimerSeconds - (int) result;
	}
	
	private void sendPacket(final int nameId, final int point) {
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (nameId != 0) {
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400237, new DescriptionId(nameId * 2 + 1), point));
				}
				PacketSendUtility.sendPacket(player, new S_INSTANT_DUNGEON_INFO(getTime(), instanceReward, null));
			}
		});
	}
	
	private int checkRank(int totalPoints) {
		if (totalPoints >= 15200) { //Rank S.
			rank = 1;
		} else if (totalPoints >= 14320) { //Rank A.
			rank = 2;
		} else if (totalPoints >= 10913) { //Rank B.
			rank = 3;
		} else if (totalPoints >= 6656) { //Rank C.
			rank = 4;
		} else if (totalPoints >= 0) { //Rank F.
			rank = 6;
		} else {
			rank = 8;
		}
		return rank;
	}
	
	protected void startInstanceTask() {
		darkPoetaTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
				instance.doOnAllPlayers(new Visitor<Player>() {
				    @Override
				    public void visit(Player player) {
					    stopInstance(player);
				    }
			    });
            }
        }, 7200000)); //...2Hrs
    }
	
	@Override
	public void onOpenDoor(Player player, int doorId) {
		if (doorId == 33) {
			startInstanceTask();
			doors.get(33).setOpen(true);
			//The member recruitment window has passed. You cannot recruit any more members.
			sendMsgByRace(1401181, Race.PC_ALL, 10000);
			//The player has 1 min to prepare !!! [Timer Red]
			if ((timerPrepare != null) && (!timerPrepare.isDone() || !timerPrepare.isCancelled())) {
				//Start the instance time !!! [Timer White]
				startMainInstanceTimer();
			}
		}
	}
	
	@Override
	public void onEnterInstance(final Player player) {
		startPrepareTimer();
	}
	
	private void startPrepareTimer() {
		if (timerPrepare == null) {
			timerPrepare = ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					startMainInstanceTimer();
				}
			}, prepareTimerSeconds);
		}
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new S_INSTANT_DUNGEON_INFO(prepareTimerSeconds, instanceReward, null));
			}
		});
	}
	
	private void startMainInstanceTimer() {
		if (!timerPrepare.isDone()) {
			timerPrepare.cancel(false);
		}
		startTime = System.currentTimeMillis();
		instanceReward.setInstanceScoreType(InstanceScoreType.START_PROGRESS);
		sendPacket(0, 0);
	}
	
	protected void stopInstance(Player player) {
        stopInstanceTask();
        instanceReward.setRank(6);
		instanceReward.setRank(checkRank(instanceReward.getPoints()));
		instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
		sendPacket(0, 0);
	}
	
	private void stopInstanceTask() {
        for (FastList.Node<Future<?>> n = darkPoetaTask.head(), end = darkPoetaTask.tail(); (n = n.getNext()) != end; ) {
            if (n.getValue() != null) {
                n.getValue().cancel(true);
            }
        }
    }
	
	protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time) {
        sp(npcId, x, y, z, h, 0, time, 0, null);
    }
	
    protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int time, final int msg, final Race race) {
        sp(npcId, x, y, z, h, 0, time, msg, race);
    }
	
    protected void sp(final int npcId, final float x, final float y, final float z, final byte h, final int entityId, final int time, final int msg, final Race race) {
        darkPoetaTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
        darkPoetaTask.add(ThreadPoolManager.getInstance().schedule(new Runnable() {
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
	
	@Override
	public void onInstanceDestroy() {
		if (timerInstance != null) {
			timerInstance.cancel(false);
		} if (timerPrepare != null) {
			timerPrepare.cancel(false);
		}
		stopInstanceTask();
		isInstanceDestroyed = true;
		doors.clear();
	}
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		instanceReward = new DarkPoetaReward(mapId, instanceId);
		instanceReward.setInstanceScoreType(InstanceScoreType.PREPARING);
		doors = instance.getDoors();
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
	}
	
	@Override
	public void onGather(Player player, Gatherable gatherable) {
		int score = 0;
		switch (gatherable.getObjectTemplate().getTemplateId()) {
		    case 401111: //Huge Vine.
			    score += 200;
			break;
			case 401112: //Nex.
			    score += 50;
			break;
		} if (instanceReward.getInstanceScoreType().isStartProgress()) {
			instanceReward.addGatherCollection();
			instanceReward.addPoints(score);
			sendPacket(gatherable.getObjectTemplate().getNameId(), score);
		}
	}
	
	protected void despawnNpc(Npc npc) {
        if (npc != null) {
            npc.getController().onDelete();
        }
    }
	
	protected void despawnNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			npc.getController().onDelete();
		}
	}
	
	private boolean isDead(Npc npc) {
		return (npc == null || npc.getLifeStats().isAlreadyDead());
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		//"Player Name" has left the battle.
		PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400255, player.getName()));
		if (player.isInGroup2()) {
            PlayerGroupService.removePlayer(player);
        }
	}
	
	@Override
	public void onExitInstance(Player player) {
		InstanceService.destroyInstance(player.getPosition().getWorldMapInstance());
		if (instanceReward.getInstanceScoreType().isEndProgress()) {
			TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
		}
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
}