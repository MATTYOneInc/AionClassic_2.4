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
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(300250000)
public class Esoterrace extends GeneralInstanceHandler
{
	private int labManagerKilled;
	private boolean isInstanceDestroyed;
	private Map<Integer, StaticDoor> doors;
	
	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		int index = dropItems.size() + 1;
		switch (npcId) {
			case 217185: //idf4re_dra_01_bioniccpu_53_ah.
				for (Player player: instance.getPlayersInside()) {
				    if (player.isOnline()) {
						dropItems.add(DropRegistrationService.getInstance().regDropItem(index++, player.getObjectId(), npcId, 185000111, 1, true));
					}
				}
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
		switch (Rnd.get(1, 2)) {
			///Esoterrace Mage OR Keening Sirokin.
			case 1:
				spawn(217649, 1035.0000f, 981.0000f, 327.0000f, (byte) 45);
				spawn(799580, 1032.0000f, 985.0000f, 327.0000f, (byte) 100);
			break;
			///Shulack Peon.
			case 2:
			    spawn(799578, 1036.0000f, 987.0000f, 327.0000f, (byte) 107);
				spawn(799578, 1038.0000f, 985.0000f, 327.0000f, (byte) 46);
			break;
		}
    }
	
    @Override
    public void onDie(Npc npc) {
        Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 282291: //Surkana Feeder.
			    despawnNpc(npc);
				despawnNpcs(instance.getNpcs(217204)); //Kexkra.
				//The Surkana Supplier has overloaded.
				sendMsgByRace(1400996, Race.PC_ALL, 0);
				//The Surkana Supplier has been broken.
				sendMsgByRace(1401037, Race.PC_ALL, 5000);
				spawn(217205, 1315.0000f, 1170.0000f, 51.0000f, (byte) 87); //Kexkra Prototype.
			break;
			case 217185: //Dalia Charlands.
			    rewardPlayerInside();
			    //Dalia Charlands has vanished.
				sendMsgByRace(1401036, Race.PC_ALL, 0);
				//The Surkana Steam Jet has generated an updraft.
				sendMsgByRace(1400997, Race.PC_ALL, 5000);
				//Defeat all Drana Production Lab Section Managers to open the Laboratory Yard door.
				sendMsgByRace(1400919, Race.PC_ALL, 120000);
				spawn(207033, 392.0000f, 543.0000f, 318.0000f, (byte) 18); //Windstream A
				spawn(207035, 392.0000f, 543.0000f, 318.0000f, (byte) 18); //Windstream B
				spawn(701023, 1264.0000f, 644.0000f, 296.0000f, (byte) 0, 112); //Large Entwined Chest.
				///Greenfingers.
				final Npc idf4re_dra_01_nameda_sum_3_53_ae = (Npc) spawn(282178, 1234.0000f, 654.0000f, 296.0000f, (byte) 0);
				idf4re_dra_01_nameda_sum_3_53_ae.getSpawn().setWalkerId("npcpathidf4re_drana_etc_1st_bridge_mob");
				WalkManager.startWalking((NpcAI2) idf4re_dra_01_nameda_sum_3_53_ae.getAi2());
				idf4re_dra_01_nameda_sum_3_53_ae.setState(1);
				PacketSendUtility.broadcastPacket(idf4re_dra_01_nameda_sum_3_53_ae, new S_ACTION(idf4re_dra_01_nameda_sum_3_53_ae, EmotionType.START_EMOTE2, 0, idf4re_dra_01_nameda_sum_3_53_ae.getObjectId()));
				actionGreenfingers(instance.getNpc(282178));
            break;
			case 217282: //Esoterrace Investigator.
			case 217283: //Senior Lab Researcher.
			case 217284: //Lab Supervisor.
				labManagerKilled++;
				if (labManagerKilled == 1) {
					doors.get(367).setOpen(false);
					spawn(282380, 1057.0000f, 889.0000f, 347.0000f, (byte) 0, 601); //Alarm Siren.
				} else if (labManagerKilled == 2) {
					doors.get(69).setOpen(false);
					spawn(282380, 1057.0000f, 895.0000f, 347.0000f, (byte) 0, 600); //Alarm Siren.
				} else if (labManagerKilled == 3) {
					doors.get(111).setOpen(true);
					//The door to the Laboratory Yard is now open.
					sendMsgByRace(1400920, Race.PC_ALL, 0);
					//The Drana Production Lab walkway is now open.
					sendMsgByRace(1400923, Race.PC_ALL, 4000);
					spawn(282380, 1057.0000f, 901.0000f, 347.0000f, (byte) 0, 599); //Alarm Siren.
				}
			break;
			case 217281: //Lab Gatekeeper.
				doors.get(70).setOpen(true);
				//The door to the Laboratory Air Conditioning Room is now open.
				sendMsgByRace(1400921, Race.PC_ALL, 0);
            break;
			case 217649: //Esoterrace Mage.
			    despawnNpc(npc);
				final Npc keeningSirokin = instance.getNpc(799580);
				keeningSirokin.getSpawn().setWalkerId("idf4re_dra_02_shulackprisoner_hidden");
				WalkManager.startWalking((NpcAI2) keeningSirokin.getAi2());
				actionKeeningSirokin(instance.getNpc(799580));
			break;
			case 217195: //Captain Murugan.
				doors.get(45).setOpen(true);
				doors.get(52).setOpen(true);
				doors.get(67).setOpen(true);
				//The Surkana Steam Jet has generated an updraft.
				sendMsgByRace(1400997, Race.PC_ALL, 0);
				spawn(207036, 392.0000f, 543.0000f, 318.0000f, (byte) 18); //Windstream C
				spawn(207037, 392.0000f, 543.0000f, 318.0000f, (byte) 18); //Windstream D
				spawn(701024, 751.0000f, 1136.000f, 365.0000f, (byte) 0, 41); //Chilled Treasure.
				spawn(701024, 827.0000f, 1136.000f, 365.0000f, (byte) 0, 77); //Chilled Treasure.
            break;
			case 282293: //Esoterrace Ventilator.
				//The Laboratory Ventilator is now open.
				sendMsgByRace(1400922, Race.PC_ALL, 0);
			break;
			case 282295: //Command Gate Control.
			    despawnNpc(npc);
				doors.get(39).setOpen(true);
			break;
			case 217289: //Esoterrace Biolab Watchman.
				doors.get(122).setOpen(true);
				//The outer wall of the Bio Lab has collapsed.
				sendMsgByRace(1400924, Race.PC_ALL, 0);
				spawn(282380, 1344.0000f, 1039.000f, 203.0000f, (byte) 0, 467); //Alarm Siren.
				spawn(282380, 1347.0000f, 1029.000f, 203.0000f, (byte) 0, 470); //Alarm Siren.
            break;
			case 217204: //Kexkra.
				spawn(701044, 1341.0000f, 1181.0000f, 51.0000f, (byte) 67); //Esoterrace Dimensional Rift Exit.
				spawn(701027, 1326.0000f, 1173.0000f, 51.0000f, (byte) 0, 726); //Laboratory Treasure Chest.
				spawn(701027, 1321.0000f, 1179.0000f, 51.0000f, (byte) 0, 727); //Laboratory Treasure Chest.
            break;
            case 217206: //Warden Surama.
				spawn(701044, 1341.0000f, 1181.0000f, 51.0000f, (byte) 67); //Esoterrace Dimensional Rift Exit.
				spawn(701027, 1326.0000f, 1173.0000f, 51.0000f, (byte) 0, 726); //Laboratory Treasure Chest.
				spawn(701027, 1321.0000f, 1179.0000f, 51.0000f, (byte) 0, 727); //Laboratory Treasure Chest.
            break;
			case 217650: //Dalia Watcher.
			    despawnNpc(npc);
			break;
        }
    }
	
	private void rewardPlayerInside() {
		int playerInside = instance.getPlayersInside().size();
		if (playerInside == 1) {
			spawn(701022, 1276.0000f, 614.0000f, 296.0000f, (byte) 0, 85);
		} else if (playerInside == 2) {
			spawn(701022, 1276.0000f, 614.0000f, 296.0000f, (byte) 0, 85);
			spawn(701022, 1291.0000f, 634.0000f, 296.0000f, (byte) 0, 86);
		} else if (playerInside == 3) {
			spawn(701022, 1276.0000f, 614.0000f, 296.0000f, (byte) 0, 85);
			spawn(701022, 1291.0000f, 634.0000f, 296.0000f, (byte) 0, 86);
			spawn(701022, 1286.0000f, 671.0000f, 296.0000f, (byte) 0, 89);
		} else if (playerInside == 4) {
			spawn(701022, 1276.0000f, 614.0000f, 296.0000f, (byte) 0, 85);
			spawn(701022, 1291.0000f, 634.0000f, 296.0000f, (byte) 0, 86);
			spawn(701022, 1286.0000f, 671.0000f, 296.0000f, (byte) 0, 89);
			spawn(701022, 1235.0000f, 651.0000f, 296.0000f, (byte) 0, 91);
		} else if (playerInside == 5) {
			spawn(701022, 1276.0000f, 614.0000f, 296.0000f, (byte) 0, 85);
			spawn(701022, 1291.0000f, 634.0000f, 296.0000f, (byte) 0, 86);
			spawn(701022, 1286.0000f, 671.0000f, 296.0000f, (byte) 0, 89);
			spawn(701022, 1235.0000f, 651.0000f, 296.0000f, (byte) 0, 91);
			spawn(701022, 1255.0000f, 629.0000f, 296.0000f, (byte) 0, 93);
		} else if (playerInside == 6) {
			spawn(701022, 1276.0000f, 614.0000f, 296.0000f, (byte) 0, 85);
			spawn(701022, 1291.0000f, 634.0000f, 296.0000f, (byte) 0, 86);
			spawn(701022, 1286.0000f, 671.0000f, 296.0000f, (byte) 0, 89);
			spawn(701022, 1235.0000f, 651.0000f, 296.0000f, (byte) 0, 91);
			spawn(701022, 1255.0000f, 629.0000f, 296.0000f, (byte) 0, 93);
			spawn(701022, 1248.0000f, 673.0000f, 296.0000f, (byte) 0, 206);
		}
	}
	
	private void actionKeeningSirokin(final Npc keeningSirokin) {
        if (keeningSirokin != null) {
			///You saved my life. Thank you very much!
			NpcShoutsService.getInstance().sendMsg(keeningSirokin, 342359, keeningSirokin.getObjectId(), 0, 3000);
			///Go to Inokin! He will repay you!
			NpcShoutsService.getInstance().sendMsg(keeningSirokin, 342360, keeningSirokin.getObjectId(), 0, 10000);
			spawn(701025, 1038.0000f, 987.0000f, 328.0000f, (byte) 0, 725);
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    if (!isInstanceDestroyed) {
                        if (keeningSirokin != null) {
							despawnNpc(keeningSirokin);
                        }
                    }
                }
            }, 20000);
        }
	}
	
	private void actionGreenfingers(final Npc greenfingers) {
        if (greenfingers != null) {
			///Dalia has fallen!
			NpcShoutsService.getInstance().sendMsg(greenfingers, 342399, greenfingers.getObjectId(), 0, 0);
			///Block the laboratory entrance!
			NpcShoutsService.getInstance().sendMsg(greenfingers, 342400, greenfingers.getObjectId(), 0, 12000);
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    if (!isInstanceDestroyed) {
                        if (greenfingers != null) {
							despawnNpc(greenfingers);
							doors.get(69).setOpen(true);
							doors.get(367).setOpen(true);
							///The Bridge to the Drana Production Lab has been raised.
							sendMsgByRace(1400918, Race.PC_ALL, 0);
							//IDF4Re_Drana_Ranger_1
							final Npc IDF4Re_Drana_Ranger_1 = (Npc) spawn(799626, 1152.0000f, 688.0000f, 295.0000f, (byte) 10);
							IDF4Re_Drana_Ranger_1.getSpawn().setWalkerId("idf4re_drana_etc_1st_bridge_npc_path_01");
							WalkManager.startWalking((NpcAI2) IDF4Re_Drana_Ranger_1.getAi2());
							IDF4Re_Drana_Ranger_1.setState(1);
							PacketSendUtility.broadcastPacket(IDF4Re_Drana_Ranger_1, new S_ACTION(IDF4Re_Drana_Ranger_1, EmotionType.START_EMOTE2, 0, IDF4Re_Drana_Ranger_1.getObjectId()));
							//IDF4Re_Drana_Ranger_2
							final Npc IDF4Re_Drana_Ranger_2 = (Npc) spawn(799627, 1153.0000f, 684.0000f, 295.0000f, (byte) 10);
							IDF4Re_Drana_Ranger_2.getSpawn().setWalkerId("idf4re_drana_etc_1st_bridge_npc_path_02");
							WalkManager.startWalking((NpcAI2) IDF4Re_Drana_Ranger_2.getAi2());
							IDF4Re_Drana_Ranger_2.setState(1);
							PacketSendUtility.broadcastPacket(IDF4Re_Drana_Ranger_2, new S_ACTION(IDF4Re_Drana_Ranger_2, EmotionType.START_EMOTE2, 0, IDF4Re_Drana_Ranger_2.getObjectId()));
                        }
                    }
                }
            }, 14000);
        }
	}
	
	private void removeItems(Player player) {
		Storage storage = player.getInventory();
		storage.decreaseByItemId(185000111, storage.getItemCountByItemId(185000111));
	}
	
	private void despawnNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			npc.getController().onDelete();
		}
	}
	
	private void despawnNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
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
	public void onLeaveInstance(Player player) {
		removeItems(player);
	}
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeItems(player);
	}
	
    @Override
    public void onInstanceDestroy() {
		isInstanceDestroyed = true;
        doors.clear();
    }
}