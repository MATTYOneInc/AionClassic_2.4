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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.shedule.RiftSchedule;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.rift.RiftLocation;
import com.aionemu.gameserver.model.templates.rift.OpenRift;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.riftspawns.RiftSpawnTemplate;
import com.aionemu.gameserver.services.rift.RiftInformer;
import com.aionemu.gameserver.services.rift.RiftManager;
import com.aionemu.gameserver.services.rift.RiftOpenRunnable;
import com.aionemu.gameserver.spawnengine.SpawnEngine;

/****/
/** Author Rinzler (Encom)
/****/

public class RiftService
{
	private Map<Integer, RiftLocation> locations;
	private final Lock closing = new ReentrantLock();
	private static final int duration = CustomConfig.RIFT_DURATION;
	private static final Logger log = LoggerFactory.getLogger(RiftService.class);
	private FastMap<Integer, RiftLocation> activeRifts = new FastMap<Integer, RiftLocation>();
	
	public void initRiftLocations() {
        if (CustomConfig.RIFT_ENABLED) {
            locations = DataManager.RIFT_DATA.getRiftLocations();
        } else {
            locations = Collections.emptyMap();
        }
    }
	
	public void initRifts() {
    	if (CustomConfig.RIFT_ENABLED) {
            RiftSchedule schedule = RiftSchedule.load();
            for (RiftSchedule.Rift rift: schedule.getRiftsList()) {
                for (OpenRift open: rift.getRift()) {
                    CronService.getInstance().schedule(new RiftOpenRunnable(rift.getWorldId(), open.spawnGuards()), open.getSchedule());
                }
            }
        }
    }
	
	public boolean isValidId(int id) {
        if (isRift(id)) {
            return RiftService.getInstance().getRiftLocations().keySet().contains(id);
        } else {
            for (RiftLocation loc : RiftService.getInstance().getRiftLocations().values()) {
                if (loc.getWorldId() == id) {
                    return true;
                }
            }
        }
        return false;
    }
	
	private boolean isRift(int id) {
        return id < 10000;
    }
	
	public boolean openRifts(int id, boolean guards) {
        if (isValidId(id)) {
            if (isRift(id)) {
                RiftLocation rift = getRiftLocation(id);
                if (rift.getSpawned().isEmpty()) {
                    openRifts(rift, guards);
                    RiftInformer.sendRiftsInfo(rift.getWorldId());
                    return true;
                }
            } else {
                boolean opened = false;
                for (RiftLocation rift: getRiftLocations().values()) {
                    if (rift.getWorldId() == id && rift.getSpawned().isEmpty()) {
                        openRifts(rift, guards);
                        opened = true;
                    }
                }
                RiftInformer.sendRiftsInfo(id);
                return opened;
            }
        }
        return false;
    }
	
	public boolean closeRifts(int id) {
        if (isValidId(id)) {
            if (isRift(id)) {
                RiftLocation rift = getRiftLocation(id);
                if (!rift.getSpawned().isEmpty()) {
                    closeRift(rift);
                    return true;
                }
            } else {
                boolean opened = false;
                for (RiftLocation rift : getRiftLocations().values()) {
                    if (rift.getWorldId() == id && !rift.getSpawned().isEmpty()) {
                        closeRift(rift);
                        opened = true;
                    }
                }
                return opened;
            }
        }
        return false;
    }
	
	public void openRifts(RiftLocation location, boolean guards) {
        location.setOpened(true);
		if (guards) {
            List<SpawnGroup2> locSpawns = DataManager.SPAWNS_DATA2.getRiftSpawnsByLocId(location.getId());
            for (SpawnGroup2 group : locSpawns) {
                for (SpawnTemplate st : group.getSpawnTemplates()) {
                    RiftSpawnTemplate template = (RiftSpawnTemplate) st;
                    location.getSpawned().add(SpawnEngine.spawnObject(template, 1));
                }
            }
		}
        RiftManager.getInstance().spawnRift(location);
        activeRifts.putEntry(location.getId(), location);
    }
	
	public void closeRift(RiftLocation location) {
		location.setOpened(false);
		for (VisibleObject obj: location.getSpawned()) {
			Npc spawned = (Npc) obj;
            if (spawned == null) {
				continue;
			}
			spawned.setDespawnDelayed(true);
			if (spawned.getAggroList().getList().isEmpty()) {
				spawned.getController().cancelTask(TaskId.RESPAWN);
				obj.getController().onDelete();
			}
		}
		location.getSpawned().clear();
	}
	
	public void closeRifts() {
        closing.lock();
        try {
            for (RiftLocation rift : activeRifts.values()) {
                closeRift(rift);
            }
            activeRifts.clear();
        }
		finally {
            closing.unlock();
        }
    }
	
	public int getDuration() {
		return duration;
	}
	
	public RiftLocation getRiftLocation(int id) {
		return locations.get(id);
	}
	
	public Map<Integer, RiftLocation> getRiftLocations() {
		return locations;
	}
	
	public static RiftService getInstance() {
		return RiftServiceHolder.INSTANCE;
	}
	
	private static class RiftServiceHolder {
		private static final RiftService INSTANCE = new RiftService();
	}
}