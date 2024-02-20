package com.aionemu.gameserver.model.templates.spawns;

import com.aionemu.commons.taskmanager.AbstractLockManager;
import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.siege.SiegeModType;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.instancerift.InstanceRiftStateType;
import com.aionemu.gameserver.model.templates.spawns.riftspawns.RiftSpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.instanceriftspawns.InstanceRiftSpawnTemplate;
import com.aionemu.gameserver.spawnengine.SpawnHandlerType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpawnGroup2 extends AbstractLockManager
{
	private static final Logger log = LoggerFactory.getLogger(SpawnGroup2.class);
	
	private int worldId;
	private int npcId;
	private int pool;
	private byte difficultId;
	private TemporarySpawn temporarySpawn;
	private int respawnTime;
	private SpawnHandlerType handlerType;
	private List<SpawnTemplate> spots = new ArrayList<SpawnTemplate>();
	private HashMap<Integer, HashMap<SpawnTemplate, Boolean>> poolUsedTemplates;
	
	public SpawnGroup2(int worldId, Spawn spawn) {
		this.worldId = worldId;
		initializing(spawn);
		for (SpawnSpotTemplate template : spawn.getSpawnSpotTemplates()) {
			SpawnTemplate spawnTemplate = new SpawnTemplate(this, template);
			if (spawn.isEventSpawn()) {
				spawnTemplate.setEventTemplate(spawn.getEventTemplate());
			}
			spots.add(spawnTemplate);
		}
	}
	
	public SpawnGroup2(int worldId, Spawn spawn, int siegeId, SiegeRace race, SiegeModType mod) {
		this.worldId = worldId;
		initializing(spawn);
		for (SpawnSpotTemplate template : spawn.getSpawnSpotTemplates()) {
			SiegeSpawnTemplate spawnTemplate = new SiegeSpawnTemplate(this, template);
			spawnTemplate.setSiegeId(siegeId);
			spawnTemplate.setSiegeRace(race);
			spawnTemplate.setSiegeModType(mod);
			spots.add(spawnTemplate);
		}
	}
	
	public SpawnGroup2(int worldId, Spawn spawn, int id) {
		this.worldId = worldId;
		initializing(spawn);
		for (SpawnSpotTemplate template : spawn.getSpawnSpotTemplates()) {
			RiftSpawnTemplate spawnTemplate = new RiftSpawnTemplate(this, template);
			spawnTemplate.setId(id);
			spots.add(spawnTemplate);
		}
	}
	
	public SpawnGroup2(int worldId, Spawn spawn, int id, InstanceRiftStateType type) {
		this.worldId = worldId;
		initializing(spawn);
		for (SpawnSpotTemplate template : spawn.getSpawnSpotTemplates()) {
			InstanceRiftSpawnTemplate spawnTemplate = new InstanceRiftSpawnTemplate(this, template);
			spawnTemplate.setId(id);
			spawnTemplate.setEStateType(type);
			spots.add(spawnTemplate);
		}
	}
	
    private void initializing(Spawn spawn) {
        temporarySpawn = spawn.getTemporarySpawn();
        respawnTime = spawn.getRespawnTime();
		pool = spawn.getPool();
		npcId = spawn.getNpcId();
		handlerType = spawn.getSpawnHandlerType();
		difficultId = spawn.getDifficultId();
		if (hasPool()) {
			poolUsedTemplates = new HashMap<Integer, HashMap<SpawnTemplate, Boolean>>();
		}
	}
	
	public SpawnGroup2(int worldId, int npcId) {
		this.worldId = worldId;
		this.npcId = npcId;
	}
	
	public List<SpawnTemplate> getSpawnTemplates() {
		return spots;
	}
	
	public void addSpawnTemplate(SpawnTemplate spawnTemplate) {
		spots.add(spawnTemplate);
	}
	
	public int getWorldId() {
		return worldId;
	}
	
	public int getNpcId() {
		return npcId;
	}
	
	public TemporarySpawn geTemporarySpawn() {
		return temporarySpawn;
	}
	
	public int getPool() {
		return pool;
	}
	
	public boolean hasPool() {
		return pool > 0;
	}
	
	public int getRespawnTime() {
		return respawnTime;
	}
	
	public void setRespawnTime(int respawnTime) {
		this.respawnTime = respawnTime;
	}
	
	public boolean isTemporarySpawn() {
		return temporarySpawn != null;
	}
	
	public SpawnHandlerType getHandlerType() {
		return handlerType;
	}
	
	public SpawnTemplate getRndTemplate(int instanceId) {
		final List<SpawnTemplate> allTemplates = spots;
		List<SpawnTemplate> templates = new ArrayList<SpawnTemplate>();
		super.readLock();
		try {
			for (SpawnTemplate template : allTemplates) {
				if (!isTemplateUsed(instanceId, template)) {
					templates.add(template);
				}
			} if (templates.size() == 0) {
				log.warn("Pool size more then spots, npcId: " + npcId + ", worldId: " + worldId);
				return null;
			}
		}
		finally {
			super.readUnlock();
		}
		SpawnTemplate spawnTemplate = templates.get(Rnd.get(0, templates.size() - 1));
		setTemplateUse(instanceId, spawnTemplate, true);
		return spawnTemplate;
	}
	
	public void setTemplateUse(int instanceId, SpawnTemplate template, boolean isUsed) {
		super.writeLock();
		try {
			HashMap<SpawnTemplate, Boolean> states = poolUsedTemplates.get(instanceId);
			if (states == null) {
				states = new HashMap<SpawnTemplate, Boolean>();
				poolUsedTemplates.put(instanceId, states);
			}
			states.put(template, isUsed);
		}
		finally {
			super.writeUnlock();
		}
	}
	
	public boolean isTemplateUsed(int instanceId, SpawnTemplate template) {
		super.readLock();
		try {
			HashMap<SpawnTemplate, Boolean> states = poolUsedTemplates.get(instanceId);
			if (states == null)
				return false;
			Boolean state = states.get(template);
			if (state == null)
				return false;
			return state;
		}
		finally {
			super.readUnlock();
		}
	}
	
	public void resetTemplates(int instanceId) {
		HashMap<SpawnTemplate, Boolean> states = poolUsedTemplates.get(instanceId);
		if (states == null)
			return;
		super.writeLock();
		try {
			for (SpawnTemplate template : states.keySet()) {
				states.put(template, false);
			}
		}
		finally {
			super.writeUnlock();
		}
	}
	
	public byte getDifficultId() {
        return difficultId;
    }
}