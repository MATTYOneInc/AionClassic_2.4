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
package com.aionemu.gameserver.services;

import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.shedule.InstanceSchedule;
import com.aionemu.gameserver.configs.shedule.InstanceSchedule.Instance;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.instancerift.InstanceRiftLocation;
import com.aionemu.gameserver.model.instancerift.InstanceRiftStateType;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.instanceriftspawns.InstanceRiftSpawnTemplate;
import com.aionemu.gameserver.services.instanceriftservice.InstanceStartRunnable;
import com.aionemu.gameserver.services.instanceriftservice.Rift;
import com.aionemu.gameserver.services.instanceriftservice.RiftInstance;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

import javolution.util.FastMap;

import java.util.*;

/**
 * @author Rinzler (Encom)
 */

public class InstanceRiftService
{
	private InstanceSchedule instanceSchedule;
	private Map<Integer, InstanceRiftLocation> instanceRift;
	private static final int duration = CustomConfig.INSTANCE_RIFT_DURATION;
	private final Map<Integer, RiftInstance<?>> activeInstanceRift = new FastMap<Integer, RiftInstance<?>>().shared();
	
	public void initInstanceLocations() {
		if (CustomConfig.INSTANCE_RIFT_ENABLED) {
			instanceRift = DataManager.INSTANCE_RIFT_DATA.getInstanceRiftLocations();
			for (InstanceRiftLocation loc: getInstanceRiftLocations().values()) {
				spawn(loc, InstanceRiftStateType.CLOSED);
			}
		} else {
			instanceRift = Collections.emptyMap();
		}
	}
	
	public void initInstance() {
		if (CustomConfig.INSTANCE_RIFT_ENABLED) {
		    instanceSchedule = InstanceSchedule.load();
		    for (Instance instance: instanceSchedule.getInstancesList()) {
			    for (String instanceTime: instance.getInstanceTimes()) {
				    CronService.getInstance().schedule(new InstanceStartRunnable(instance.getId()), instanceTime);
			    }
			}
		}
	}
	
	public void startInstanceRift(final int id) {
		final RiftInstance<?> rift;
		synchronized (this) {
			if (activeInstanceRift.containsKey(id)) {
				return;
			}
			rift = new Rift(instanceRift.get(id));
			activeInstanceRift.put(id, rift);
		}
		rift.start();
		instanceRiftMsg(id);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				stopInstanceRift(id);
			}
		}, duration * 3600 * 1000);
	}
	
	public void stopInstanceRift(int id) {
		if (!isInstanceRiftInProgress(id)) {
			return;
		}
		RiftInstance<?> rift;
		synchronized (this) {
			rift = activeInstanceRift.remove(id);
		} if (rift == null || rift.isClosed()) {
			return;
		}
		rift.stop();
	}
	
	public void spawn(InstanceRiftLocation loc, InstanceRiftStateType estate) {
		if (estate.equals(InstanceRiftStateType.OPEN)) {
		}
		List<SpawnGroup2> locSpawns = DataManager.SPAWNS_DATA2.getInstanceRiftSpawnsByLocId(loc.getId());
		for (SpawnGroup2 group : locSpawns) {
			for (SpawnTemplate st : group.getSpawnTemplates()) {
				InstanceRiftSpawnTemplate instanceRifttemplate = (InstanceRiftSpawnTemplate) st;
				if (instanceRifttemplate.getEStateType().equals(estate)) {
					loc.getSpawned().add(SpawnEngine.spawnObject(instanceRifttemplate, 1));
				}
			}
		}
	}
	
	public boolean instanceRiftMsg(int id) {
        switch (id) {
            case 1:
                World.getInstance().doOnAllPlayers(new Visitor<Player>() {
					@Override
					public void visit(Player player) {
						///A dimensional corridor that leads to the Indratu Fortress has appeared.
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_PORTAL_OPEN_IDLF3_Castle_Indratoo, 0);
						///A dimensional corridor that leads to the Draupnir Cave has appeared.
						PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_PORTAL_OPEN_IDDF3_Dragon, 10000);
					}
				});
			    return true;
            default:
                return false;
        }
    }
	
	public void despawn(InstanceRiftLocation loc) {
		for (VisibleObject npc : loc.getSpawned()) {
			((Npc) npc).getController().cancelTask(TaskId.RESPAWN);
			npc.getController().onDelete();
		}
		loc.getSpawned().clear();
	}
	
	public boolean isInstanceRiftInProgress(int id) {
		return activeInstanceRift.containsKey(id);
	}
	
	public Map<Integer, RiftInstance<?>> getActiveInstanceRift() {
		return activeInstanceRift;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public InstanceRiftLocation getInstanceRiftLocation(int id) {
		return instanceRift.get(id);
	}
	
	public Map<Integer, InstanceRiftLocation> getInstanceRiftLocations() {
		return instanceRift;
	}
	
	public static InstanceRiftService getInstance() {
		return InstanceRiftServiceHolder.INSTANCE;
	}
	
	private static class InstanceRiftServiceHolder {
		private static final InstanceRiftService INSTANCE = new InstanceRiftService();
	}
}