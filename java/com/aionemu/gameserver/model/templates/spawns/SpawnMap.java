package com.aionemu.gameserver.model.templates.spawns;

import com.aionemu.gameserver.model.templates.spawns.riftspawns.RiftSpawn;
import com.aionemu.gameserver.model.templates.spawns.siegespawns.SiegeSpawn;
import com.aionemu.gameserver.model.templates.spawns.instanceriftspawns.InstanceRiftSpawn;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "SpawnMap")
public class SpawnMap
{
	@XmlElement(name = "spawn")
	private List<Spawn> spawns;
	
	@XmlElement(name = "siege_spawn")
	private List<SiegeSpawn> siegeSpawns;
	
	@XmlElement(name = "rift_spawn")
	private List<RiftSpawn> riftSpawns;
	
	@XmlElement(name = "instance_rift_spawn")
	private List<InstanceRiftSpawn> instanceRiftSpawns;
	
	@XmlAttribute(name = "map_id")
	private int mapId;
	
	public SpawnMap() {
	}
	
	public SpawnMap(int mapId) {
		this.mapId = mapId;
	}
	
	public int getMapId() {
		return mapId;
	}
	
	public List<Spawn> getSpawns() {
		if (spawns == null) {
			spawns = new ArrayList<Spawn>();
		}
		return spawns;
	}
	
	public void addSpawns(Spawn spawns) {
		getSpawns().add(spawns);
	}
	
	public void removeSpawns(Spawn spawns) {
		getSpawns().remove(spawns);
	}
	
	public List<SiegeSpawn> getSiegeSpawns() {
		if (siegeSpawns == null) {
			siegeSpawns = new ArrayList<SiegeSpawn>();
		}
		return siegeSpawns;
	}
	
	public List<RiftSpawn> getRiftSpawns() {
		if (riftSpawns == null) {
			riftSpawns = new ArrayList<RiftSpawn>();
		}
		return riftSpawns;
	}
	
	public List<InstanceRiftSpawn> getInstanceRiftSpawns() {
		if (instanceRiftSpawns == null) {
			instanceRiftSpawns = new ArrayList<InstanceRiftSpawn>();
		}
		return instanceRiftSpawns;
	}
	
	public void addSiegeSpawns(SiegeSpawn spawns) {
		getSiegeSpawns().add(spawns);
	}
	
	public void addRiftSpawns(RiftSpawn spawns) {
		getRiftSpawns().add(spawns);
	}
	
	public void addInstanceRiftSpawns(InstanceRiftSpawn spawns) {
		getInstanceRiftSpawns().add(spawns);
	}
}