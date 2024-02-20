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

import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.drop.DropItem;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@InstanceID(320130000)
public class Adma_Stronghold extends GeneralInstanceHandler
{
	private boolean isInstanceDestroyed;
	private Map<Integer, StaticDoor> doors;
	
	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().getCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
		switch (npcId) {
			case 214691: //adma_graveknightnamed_50_ae.
			case 214692: //adma_vampirenamed_50_ae.
			case 214693: //adma_succubusnamed_50_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000032, 1, true));
		    break;
			case 214695: //adma_pricessnamed_50_ah.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000028, 1, true));
			break;
			case 214697: //adma_testresultzombienamedk_50_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000026, 1, true));
		    break;
			case 214698: //adma_lichhighnamedk_50_ae.
				dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000027, 1, true));
		    break;
			case 214699: //adma_vampirenamedk_50_ae.
			    switch (Rnd.get(1, 3)) {
				    case 1:
				        dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000029, 1, true));
				    break;
				    case 2:
				        dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000030, 1, true));
				    break;
				    case 3:
				        dropItems.add(DropRegistrationService.getInstance().regDropItem(1, 0, npcId, 185000031, 1, true));
				    break;
			    }
		    break;
		}
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
		///Steward Gutorum.
		switch (Rnd.get(1, 2)) {
		    case 1:
				spawn(214697, 407.9036f, 222.6649f, 164.0067f, (byte) 60);
			break;
			case 2:
				spawn(214697, 494.1243f, 223.8426f, 164.0067f, (byte) 0);
			break;
		}
		///Treasure Box.
		switch (Rnd.get(1, 3)) {
		    case 1:
				spawn(214715, 431.3800f, 658.8900f, 161.7433f, (byte) 0);
			break;
			case 2:
				spawn(214716, 338.3571f, 670.5813f, 168.8987f, (byte) 0);
			break;
			case 3:
				spawn(214717, 562.7898f, 697.5335f, 173.4860f, (byte) 0);
			break;
		}
    }
	
	@Override
	public void onEnterInstance(final Player player) {
		super.onInstanceCreate(instance);
	}
	
    @Override
    public void onDie(Npc npc) {
        Player player = npc.getAggroList().getMostPlayerDamage();
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 214691: //Captain Mundirve.
				rewardPlayerInside();
            break;
			case 214696: //Lord Lannok.
				spawn(730176, 626.144104f, 745.713867f, 200.999100f, (byte) 0, 66);
            break;
		}
    }
	
	@Override
	public void handleUseItemFinish(final Player player, final Npc npc) {
		switch (npc.getNpcId()) {
			case 700396: //Ntuamu's Teddy Bear.
			    instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						player.getEffectController().removeEffect(18464); //Happy Memory.
					}
				});
			break;
			case 700397: //Tarnished Incense Burner.
				instance.doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						player.getEffectController().removeEffect(18465); //Scent Of Stability.
					}
				});
			break;
			case 730175: //Wreck Of Unstable.
				final QuestState qs2094 = player.getQuestStateList().getQuestState(2094);
				if (qs2094 == null || qs2094.getStatus() != QuestStatus.COMPLETE) {
					PacketSendUtility.sendPacket(player, new S_NPC_HTML_MESSAGE(npc.getObjectId(), 27));
				} else {
					wreckOfUnstable(player, 586.0000f, 759.0000f, 198.0000f, (byte) 98);
				}
			break;
			case 730167: //Adma Stronghold Warehouse Backdoor.
			    warehouseBackdoorExit(player, 1839.0000f, 887.0000f, 59.0000f, (byte) 60);
			break;
			case 730166: //Adma Stronghold Exit.
			case 730176: //Wreck Of Unstable Exit.
			    if (player.getRace() == Race.ELYOS) {
					admaStrongholdExitE(player, 2238.0000f, 2284.0000f, 50.0000f, (byte) 0);
				} else {
					admaStrongholdExitA(player, 2343.0000f, 160.0000f, 246.0000f, (byte) 0);
				}
			break;
		}
	}
	
	private void rewardPlayerInside() {
		int playerInside = instance.getPlayersInside().size();
		if (playerInside == 1) {
			spawn(214715, 346.5773f, 534.6947f, 181.2040f, (byte) 40);
		} else if (playerInside == 2) {
			spawn(214715, 346.5773f, 534.6947f, 181.2040f, (byte) 40);
			spawn(214715, 345.4597f, 544.2697f, 182.1811f, (byte) 71);
		} else if (playerInside == 3) {
			spawn(214715, 346.5773f, 534.6947f, 181.2040f, (byte) 40);
			spawn(214715, 345.4597f, 544.2697f, 182.1811f, (byte) 71);
			spawn(214716, 359.0080f, 557.2267f, 181.3445f, (byte) 79);
		} else if (playerInside == 4) {
			spawn(214715, 346.5773f, 534.6947f, 181.2040f, (byte) 40);
			spawn(214715, 345.4597f, 544.2697f, 182.1811f, (byte) 71);
			spawn(214716, 359.0080f, 557.2267f, 181.3445f, (byte) 79);
			spawn(214716, 349.1757f, 519.8128f, 181.2789f, (byte) 15);
		} else if (playerInside == 5) {
			spawn(214715, 346.5773f, 534.6947f, 181.2040f, (byte) 40);
			spawn(214715, 345.4597f, 544.2697f, 182.1811f, (byte) 71);
			spawn(214716, 359.0080f, 557.2267f, 181.3445f, (byte) 79);
			spawn(214716, 349.1757f, 519.8128f, 181.2789f, (byte) 15);
			spawn(214717, 344.2627f, 525.7356f, 180.6809f, (byte) 69);
		} else if (playerInside == 6) {
			spawn(214715, 346.5773f, 534.6947f, 181.2040f, (byte) 40);
			spawn(214715, 345.4597f, 544.2697f, 182.1811f, (byte) 71);
			spawn(214716, 359.0080f, 557.2267f, 181.3445f, (byte) 79);
			spawn(214716, 349.1757f, 519.8128f, 181.2789f, (byte) 15);
			spawn(214717, 344.2627f, 525.7356f, 180.6809f, (byte) 69);
			spawn(214717, 356.2736f, 556.9730f, 180.7471f, (byte) 12);
		}
	}
	
	protected void wreckOfUnstable(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, mapId, instanceId, x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	protected void admaStrongholdExitE(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 210060000, 1, x, y, z, h);
	}
	protected void admaStrongholdExitA(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 220050000, 1, x, y, z, h);
	}
	protected void warehouseBackdoorExit(Player player, float x, float y, float z, byte h) {
		TeleportService2.teleportTo(player, 220050000, 1, x, y, z, h);
	}
	
	public void removeItems(Player player) {
        Storage storage = player.getInventory();
        storage.decreaseByItemId(185000026, storage.getItemCountByItemId(185000026));
		storage.decreaseByItemId(185000027, storage.getItemCountByItemId(185000027));
        storage.decreaseByItemId(185000028, storage.getItemCountByItemId(185000028));
		storage.decreaseByItemId(185000029, storage.getItemCountByItemId(185000029));
		storage.decreaseByItemId(185000030, storage.getItemCountByItemId(185000030));
		storage.decreaseByItemId(185000031, storage.getItemCountByItemId(185000031));
		storage.decreaseByItemId(185000032, storage.getItemCountByItemId(185000032));
    }
	
	@Override
	public void onPlayerLogOut(Player player) {
		removeItems(player);
	}
	
	@Override
	public void onLeaveInstance(Player player) {
		removeItems(player);
	}
	
	protected void despawnNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			npc.getController().onDelete();
		}
	}
	
	protected Npc getNpc(int npcId) {
		if (!isInstanceDestroyed) {
			return instance.getNpc(npcId);
		}
		return null;
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