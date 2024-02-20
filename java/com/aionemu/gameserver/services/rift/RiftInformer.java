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
package com.aionemu.gameserver.services.rift;

import com.aionemu.gameserver.controllers.RVController;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.serverpackets.S_WORLD_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;
import javolution.util.FastMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/****/
/** Author Rinzler (Encom)
/****/

public class RiftInformer
{
	public static ConcurrentLinkedQueue<Npc> getSpawned(int worldId) {
		ConcurrentLinkedQueue<Npc> rifts = RiftManager.getSpawned();
		ConcurrentLinkedQueue<Npc> worldRifts = new ConcurrentLinkedQueue<Npc>();
		for (Npc rift : rifts) {
			if (rift.getWorldId() == worldId) {
				worldRifts.add(rift);
			}
		}
		return worldRifts;
	}
	
	public static void sendRiftsInfo(int worldId) {
		syncRiftsState(worldId, getPackets(worldId));
		int twinId = getTwinId(worldId);
		if (twinId > 0) {
			syncRiftsState(twinId, getPackets(twinId));
		}
	}
	
	public static void sendRiftsInfo(Player player) {
		syncRiftsState(player, getPackets(player.getWorldId()));
		int twinId = getTwinId(player.getWorldId());
		if (twinId > 0) {
			syncRiftsState(twinId, getPackets(twinId));
		}
	}
	
	public static void sendRiftInfo(int[] worlds) {
		for (int worldId : worlds) {
			syncRiftsState(worldId, getPackets(worlds[0], -1));
		}
	}
	
	public static void sendRiftDespawn(int worldId, int objId) {
		syncRiftsState(worldId, getPackets(worldId, objId), true);
	}
	
	private static List<AionServerPacket> getPackets(int worldId) {
		return getPackets(worldId, 0);
	}
	
	private static List<AionServerPacket> getPackets(int worldId, int objId) {
		List<AionServerPacket> packets = new ArrayList<AionServerPacket>();
		if (objId == -1) {
			for (Npc rift : getSpawned(worldId)) {
				RVController controller = (RVController) rift.getController();
				if (!controller.isMaster()) {
					continue;
				}
				packets.add(new S_WORLD_INFO(controller, false));
			}
		} else {
			packets.add(new S_WORLD_INFO(getAnnounceData(worldId)));
			for (Npc rift : getSpawned(worldId)) {
				RVController controller = (RVController) rift.getController();
				if (!controller.isMaster()) {
					continue;
				}
				packets.add(new S_WORLD_INFO(controller, true));
				packets.add(new S_WORLD_INFO(controller, false));
			}
		}
		return packets;
	}
	
	private static void syncRiftsState(Player player, final List<AionServerPacket> packets) {
		for (AionServerPacket packet : packets) {
			PacketSendUtility.sendPacket(player, packet);
		}
	}
	
	private static void syncRiftsState(int worldId, final List<AionServerPacket> packets) {
		syncRiftsState(worldId, packets, false);
	}
	
	private static void syncRiftsState(int worldId, final List<AionServerPacket> packets, final boolean isDespawnInfo) {
		World.getInstance().getWorldMap(worldId).getMainWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				syncRiftsState(player, packets);
			}
		});
	}
	
	private static FastMap<Integer, Integer> getAnnounceData(int worldId) {
		FastMap<Integer, Integer> localRifts = new FastMap<Integer, Integer>();
		for (int i = 0; i < 7; i++) {
			localRifts.put(i, 0);
		} for (Npc rift : getSpawned(worldId)) {
			RVController rc = (RVController) rift.getController();
			localRifts = calcRiftsData(rc, localRifts);
		}
		return localRifts;
	}
	
	private static FastMap<Integer, Integer> calcRiftsData(RVController rift, FastMap<Integer, Integer> local) {
		if (rift.isMaster()) {
			local.putEntry(0, local.get(0) + 1);
            local.putEntry(2, local.get(2) + 1);
			local.putEntry(3, local.get(3) + 1);
            local.putEntry(4, local.get(4) + 1);
        } else {
            local.putEntry(5, local.get(5) + 1);
            local.putEntry(6, local.get(6) + 1);
		}
		return local;
	}
	
	private static int getTwinId(int worldId) {
		switch (worldId) {
		   /**
			* Elyos
			*/
			case 210020000:	//Eltnen -> Morheim
				return 220020000;
			case 210040000: //Heiron -> Beluslan
				return 220040000;
			case 210050000: //Inggison -> Gelkmaros
				return 220070000;
		   /**
			* Asmodians
			*/
			case 220020000: //Morheim -> Eltnen
				return 210020000;
			case 220040000: //Beluslan -> Heiron
				return 210040000;
			case 220070000: //Gelkmaros -> Inggison
				return 210050000;
			default:
				return 0;
		}
	}
}