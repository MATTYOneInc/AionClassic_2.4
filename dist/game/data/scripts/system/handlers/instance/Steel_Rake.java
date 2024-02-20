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
import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.FastMap;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300100000)
public class Steel_Rake extends GeneralInstanceHandler
{
	private boolean isInstanceDestroyed;
	private Map<Integer, StaticDoor> doors;
	
	protected final int BNMA_DRANASTEAM_TA = 18153;
	
	@Override
    public void onDropRegistered(Npc npc) {
        Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
        switch (npcId) {
			case 215056: //idship_krallkeeperknmd_44_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000049, 1, true));
			break;
			case 215058: //idship_shulackwihknmd_45_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000055, 1, true));
			break;
			case 215066: //idship_shulackashardknmd_45_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000050, 1, true));
			break;
			case 215067: //idship_tesinonseamanmknmd_44_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000052, 1, true));
			break;
			case 215069: //idship_shulackasfirstmateknmd_45_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000053, 1, true));
			break;
			case 215070: //idship_shulackraatillerychknmd_45_ah.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000074, 1, true));
			break;
			case 215079: //idship_mandurigiantnmd_43_ah.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 164000193, 1, true));
			break;
			case 215080: //idship_shulackengineernmd_45_ah.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000051, 1, true));
			break;
			case 215081: //idship_shulackcaptainnmd_46_ah.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000075, 1, true));
			break;
			case 215401: //idship_shulackfihardknmd_45_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000048, 1, true));
			break;
			case 215411: //idship_shulackraknmd_44_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000073, 1, true));
			break;
        }
    }
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
	}
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		doors = instance.getDoors();
		instance.doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				player.getController().updateZone();
				player.getController().updateNearbyQuests();
			}
		});
		///Timid Alakin.
		final Npc idship_shulackwihknmd_45_ae = (Npc) spawn(215058, 461.2178f, 537.5136f, 886.01807f, (byte) 0);
		idship_shulackwihknmd_45_ae.getSpawn().setWalkerId("npcpathidshulackship_npc46");
		WalkManager.startWalking((NpcAI2) idship_shulackwihknmd_45_ae.getAi2());
		///Largimark The Smoker OR Hook The One-Armed.
		switch (Rnd.get(1, 2)) {
		    case 1:
				spawn(215067, 522.149292f, 466.552094f, 1021.469727f, (byte) 0);
				spawn(215068, 635.792786f, 541.592896f, 1031.469727f, (byte) 60);
			break;
			case 2:
				spawn(215068, 522.149292f, 466.552094f, 1021.469727f, (byte) 0);
				spawn(215067, 635.792786f, 541.592896f, 1031.469727f, (byte) 60);
			break;
		}
		///Special Delivery.
		switch (Rnd.get(1, 6)) {
			case 1:
				spawn(215054, 461.933350f, 510.545654f, 877.618103f, (byte) 90);
			break;
			case 2:
				spawn(215055, 461.933350f, 510.545654f, 877.618103f, (byte) 90);
			break;
			case 3:
				spawn(215074, 461.933350f, 510.545654f, 877.618103f, (byte) 90);
			break;
			case 4:
				spawn(215075, 461.933350f, 510.545654f, 877.618103f, (byte) 90);
			break;
			case 5:
				spawn(215076, 461.933350f, 510.545654f, 877.618103f, (byte) 90);
			break;
			case 6:
				spawn(215077, 461.933350f, 510.545654f, 877.618103f, (byte) 90);
			break;
		}
		///Treasure Box.
		switch (Rnd.get(1, 2)) {
			case 1:
				spawn(215420, 451.1610f, 575.9381f, 887.4099f, (byte) 0);
				spawn(215421, 471.0945f, 576.6632f, 887.4600f, (byte) 0);
				spawn(215420, 472.5882f, 514.1755f, 877.6181f, (byte) 0);
				spawn(215421, 450.9453f, 515.0083f, 877.6181f, (byte) 0);
			break;
			case 2:
				spawn(215421, 451.1610f, 575.9381f, 887.4099f, (byte) 0);
				spawn(215420, 471.0945f, 576.6632f, 887.4600f, (byte) 0);
				spawn(215421, 472.5882f, 514.1755f, 877.6181f, (byte) 0);
				spawn(215420, 450.9453f, 515.0083f, 877.6181f, (byte) 0);
			break;
		}
	}
	
	@Override
	public void onDie(Npc npc)  {
		Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 215058: //Timid Alakin.
				///The door is open and you can now access The Brig.
				sendMsgByRace(1400249, Race.PC_ALL, 2000);
			break;
			case 215066: //Technician Binukin.
				///The door is open and you can now access the Drana Generator Chamber.
				sendMsgByRace(1400250, Race.PC_ALL, 2000);
			break;
			case 215080: //Engineer Lahulahu.
				removeDranaSteam(player);
				despawnNpcs(instance.getNpcs(281108));
				spawn(800420, 239.70494f, 515.47736f, 948.67365f, (byte) 85); //Space Rift.
			break;
			case 215401: //Sturdy Bubukin.
			    ///Pegureronerk OR Kaneron Agent.
				switch (Rnd.get(1, 2)) {
					case 1:
						spawn(798376, 516.3097f, 489.7558f, 885.7602f, (byte) 56);
					break;
					case 2:
						spawn(215041, 516.3097f, 489.7558f, 885.7602f, (byte) 56);
					break;
				}
			break;
			case 215411: //Zerkin The One-Eyed.
				///The door is open and you can now access the Large Gun Deck.
				sendMsgByRace(1400251, Race.PC_ALL, 2000);
			break;
			case 281181:
			case 281182:
			case 281183:
			case 281184:
			    despawnNpc(npc);
			break;
		}
	}
	
	@Override
   	public void checkDistance(final Player player, final Npc npc) {
		switch(npc.getNpcId()) {
			case 206000: //shugo_shulack_01_body.
			    if (MathUtil.isIn3dRange(npc, player, 10)) {
				    despawnNpc(npc);
					if (player.getRace() == Race.ELYOS) {
						final QuestState qs3210 = player.getQuestStateList().getQuestState(3210);
						if (qs3210 != null && qs3210.getStatus() == QuestStatus.START && qs3210.getQuestVarById(0) == 0) {
							despawnNpcs(instance.getNpcs(798332));
							spawn(798333, 407.7784f, 497.0430f, 885.9434f, (byte) 40);
						}
					} else {
						final QuestState qs4210 = player.getQuestStateList().getQuestState(4210);
						if (qs4210 != null && qs4210.getStatus() == QuestStatus.START && qs4210.getQuestVarById(0) == 0) {
							despawnNpcs(instance.getNpcs(798332));
							spawn(798333, 407.7784f, 497.0430f, 885.9434f, (byte) 40);
						}
					}
				}
			break;
		}
    }
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			case 700548: //Main Deck Mobile Cannon.
			    if (player.getInventory().decreaseByItemId(185000052, 1)) { //Largimark's Flint.
					SkillEngine.getInstance().getSkill(npc, 18572, 60, npc).useNoAnimationSkill();
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							killNpc(getNpcs(214968));
						    killNpc(getNpcs(215402));
						    killNpc(getNpcs(215403));
						    killNpc(getNpcs(215404));
						    killNpc(getNpcs(215405));
							final Npc idship_shulackasfirstmateknmd_45_ae = (Npc) spawn(215069, 474.1537f, 508.9921f, 1032.8387f, (byte) 0);
					        idship_shulackasfirstmateknmd_45_ae.getSpawn().setWalkerId("idship_mobpath_shulackasfirstmateknmd_45_ae");
					        WalkManager.startWalking((NpcAI2) idship_shulackasfirstmateknmd_45_ae.getAi2());
						}
					}, 3000);
				} else {
					///I'll need Largimark's Flint.
			        PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(false, 1111302, player.getObjectId(), 2));
				}
			break;
			case 700509: ///Shining Box https://aioncodex.com/3x/quest/1097-2097/
				switch (player.getRace()) {
				    case ELYOS:
						final QuestState qs1097 = player.getQuestStateList().getQuestState(1097); //Sword Of Transcendence.
						if (qs1097 != null && qs1097.getStatus() == QuestStatus.START && qs1097.getQuestVarById(0) == 2) {
							instance.doOnAllPlayers(new Visitor<Player>() {
								@Override
								public void visit(Player player) {
									if (player.isOnline()) {
										ItemService.addItem(player, 182206059, 1);
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
										ItemService.addItem(player, 182207086, 1);
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
			case 700522: ///Haorunerk's Bag https://aioncodex.com/3x/quest/3200-4200/
				switch (player.getRace()) {
				    case ELYOS:
						final QuestState qs3200 = player.getQuestStateList().getQuestState(3200); //Price Of Goodwill.
						if (qs3200 != null && qs3200.getStatus() == QuestStatus.START && qs3200.getQuestVarById(0) == 2) {
							instance.doOnAllPlayers(new Visitor<Player>() {
								@Override
								public void visit(Player player) {
									if (player.isOnline()) {
										ItemService.addItem(player, 182209082, 1);
									}
								}
							});
						} else {
							///You have not acquired this quest.
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
						}
					break;
					case ASMODIANS:
						final QuestState qs4200 = player.getQuestStateList().getQuestState(4200); //A Suspicious Call.
						if (qs4200 != null && qs4200.getStatus() == QuestStatus.START && qs4200.getQuestVarById(0) == 2) {
							instance.doOnAllPlayers(new Visitor<Player>() {
								@Override
								public void visit(Player player) {
									if (player.isOnline()) {
										ItemService.addItem(player, 182209097, 1);
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
			case 700562: ///Strongbox https://aioncodex.com/3x/quest/3930-4934/
				switch (player.getRace()) {
				    case ELYOS:
						final QuestState qs3930 = player.getQuestStateList().getQuestState(3930); //The Shattered Stigma.
						if (qs3930 != null && qs3930.getStatus() == QuestStatus.START && qs3930.getQuestVarById(0) == 2) {
							instance.doOnAllPlayers(new Visitor<Player>() {
								@Override
								public void visit(Player player) {
									if (player.isOnline()) {
										ItemService.addItem(player, 182206075, 1);
									}
								}
							});
						} else {
							///You have not acquired this quest.
							PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1390254));
						}
					break;
					case ASMODIANS:
						final QuestState qs4934 = player.getQuestStateList().getQuestState(4934); //The Shulack's Stigma.
						if (qs4934 != null && qs4934.getStatus() == QuestStatus.START && qs4934.getQuestVarById(0) == 2) {
							instance.doOnAllPlayers(new Visitor<Player>() {
								@Override
								public void visit(Player player) {
									if (player.isOnline()) {
										ItemService.addItem(player, 182207102, 1);
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
			case 730200: //idship_1f_bigcage_indoor.
			    if (player.getInventory().decreaseByItemId(185000055, 1)) {
				    IDShip1FBigCageInDoor(player, 461.0000f, 489.0000f, 879.0000f, (byte) 0);
				} else {
					///You need a key to open the door.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300723));
				}
			break;
			case 730201: //idship_1f_bigcage_outdoor.
				IDShip1FBigCageOutDoor(player, 461.0000f, 486.0000f, 879.0000f, (byte) 0);
			break;
			case 730202: //idship_1f_bossroom_door.
			    if (player.getInventory().decreaseByItemId(185000050, 1)) {
				    IDShip1FBossRoomDoor(player, 660.0000f, 509.0000f, 870.0000f, (byte) 0);
				} else {
					///You need a key to open the door.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300723));
				}
			break;
			case 730203: //idship_3f_cannon_indoor.
			    if (player.getInventory().decreaseByItemId(185000073, 1)) {
				    IDShip3FCannonInDoor(player, 725.0000f, 508.0000f, 1014.0000f, (byte) 0);
				} else {
					///You need a key to open the door.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300723));
				}
			break;
			case 730204: //idship_3f_cannon_outdoor.
				IDShip3FCannonOutDoor(player, 720.0000f, 508.0000f, 1014.0000f, (byte) 0);
			break;
			case 730205: //idship_3f_bossroom_door.
			    IDShip3FBossRoomDoor(player, 384.0000f, 530.0000f, 1073.0000f, (byte) 0);
			break;
			case 730207: //idship_teleport_cannon.
				player.setState(CreatureState.FLIGHT_TELEPORT);
				player.unsetState(CreatureState.ACTIVE);
				player.setFlightTeleportId(73001);
				PacketSendUtility.sendPacket(player, new S_ACTION(player, EmotionType.START_FLYTELEPORT, 73001, 0));
			break;
			case 730209: //idship_anchor.
			    IDShipAnchor(player, 709.0000f, 459.0000f, 1015.0000f, (byte) 0);
			break;
			case 730210: //idship_lever.
			    IDShipLeverAliasDown(player, 283.0000f, 460.0000f, 953.0000f, (byte) 0);
			break;
			case 730216: //idship_lever_01.
			    IDShipLeverAliasUp(player, 283.0000f, 453.0000f, 903.0000f, (byte) 0);
			break;
			case 700525: //idshulackship_hatch_q3212.
			case 730198: //idship_maindoor.
			case 730206: //idship_bossroom_exit.
				if (player.getRace() == Race.ELYOS) {
					steelRakeExitE(player, 1865.0000f, 2069.0000f, 517.0000f, (byte) 112);
				} else {
					steelRakeExitA(player, 953.0000f, 1154.0000f, 195.0000f, (byte) 48);
				}
			break;
			case 800420: //idship_teleport_3f.
				IDShip_Teleport_3F(player, 622.0000f, 447.0000f, 1031.0000f, (byte) 49);
			break;
		}
	}
	
	protected void IDShip1FBigCageInDoor(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void IDShip1FBigCageOutDoor(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void IDShip1FBossRoomDoor(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void IDShip3FCannonInDoor(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void IDShip3FCannonOutDoor(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void IDShip3FBossRoomDoor(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void IDShip_Teleport_3F(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void IDShipAnchor(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void IDShipLeverAliasDown(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void IDShipLeverAliasUp(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h);
	}
	protected void steelRakeExitE(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 110010000, 1, x, y, z, h);
	}
	protected void steelRakeExitA(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 120010000, 1, x, y, z, h);
	}
	
	private void removeDranaSteam(Player player) {
		PlayerEffectController effectController = player.getEffectController();
		effectController.removeEffect(BNMA_DRANASTEAM_TA);
	}
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}
	
	private void despawnNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			npc.getController().onDelete();
		}
	}
	
	protected List<Npc> getNpcs(int npcId) {
		if (!isInstanceDestroyed) {
			return instance.getNpcs(npcId);
		}
		return null;
	}
	
	protected void killNpc(List<Npc> npcs) {
        for (Npc npc: npcs) {
            npc.getController().die();
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
	
	@Override
    public void onInstanceDestroy() {
        doors.clear();
    }
}