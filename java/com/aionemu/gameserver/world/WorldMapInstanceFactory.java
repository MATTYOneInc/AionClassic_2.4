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
package com.aionemu.gameserver.world;

import com.aionemu.gameserver.instance.InstanceEngine;
import com.aionemu.gameserver.instance.handlers.InstanceHandler;

public class WorldMapInstanceFactory
{
	public static WorldMapInstance createWorldMapInstance(WorldMap parent, int instanceId) {
		return createWorldMapInstance(parent, instanceId, 0);
	}
	
	public static WorldMapInstance createWorldMapInstance(WorldMap parent, int instanceId, int ownerId) {
		WorldMapInstance worldMapInstance = null;
		if (parent.getMapId() == WorldMapType.RESHANTA.getId()) {
			worldMapInstance = new WorldMap3DInstance(parent, instanceId);
		} else {
			worldMapInstance = new WorldMap2DInstance(parent, instanceId, ownerId);
		}
		InstanceHandler instanceHandler = InstanceEngine.getInstance().getNewInstanceHandler(parent.getMapId());
		worldMapInstance.setInstanceHandler(instanceHandler);
		return worldMapInstance;
	}
}