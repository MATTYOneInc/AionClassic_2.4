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
package com.aionemu.gameserver.model.templates.spawns.instanceriftspawns;

import com.aionemu.gameserver.model.instancerift.InstanceRiftStateType;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnSpotTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;

/**
 * @author Rinzler (Encom)
 */

public class InstanceRiftSpawnTemplate extends SpawnTemplate
{
	private int id;
	private InstanceRiftStateType instanceRiftType;
	
	public InstanceRiftSpawnTemplate(SpawnGroup2 spawnGroup, SpawnSpotTemplate spot) {
		super(spawnGroup, spot);
	}
	
	public InstanceRiftSpawnTemplate(SpawnGroup2 spawnGroup, float x, float y, float z, byte heading, int randWalk, String walkerId, int entityId, int fly) {
		super(spawnGroup, x, y, z, heading, randWalk, walkerId, entityId, fly);
	}
	
	public int getId() {
		return id;
	}
	
	public InstanceRiftStateType getEStateType() {
		return instanceRiftType;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setEStateType(InstanceRiftStateType instanceRiftType) {
		this.instanceRiftType = instanceRiftType;
	}
	
	public final boolean isInstanceRiftOpen() {
		return instanceRiftType.equals(InstanceRiftStateType.OPEN);
	}
	
	public final boolean isInstanceRiftClosed() {
		return instanceRiftType.equals(InstanceRiftStateType.CLOSED);
	}
}