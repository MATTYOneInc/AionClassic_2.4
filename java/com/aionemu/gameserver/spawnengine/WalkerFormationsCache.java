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
package com.aionemu.gameserver.spawnengine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Rolandas
 */
class WalkerFormationsCache {

	private static Map<Integer, WorldWalkerFormations> formations = new ConcurrentHashMap<>();

	private WalkerFormationsCache() {
	}

	protected static InstanceWalkerFormations getInstanceFormations(int worldId, int instanceId) {
		WorldWalkerFormations wwf = formations.get(worldId);
		if (wwf == null) {
			wwf = new WorldWalkerFormations();
			formations.put(worldId, wwf);
		}
		return wwf.getInstanceFormations(instanceId);
	}

	protected static void onInstanceDestroy(int worldId, int instanceId) {
		getInstanceFormations(worldId, instanceId).onInstanceDestroy();
	}

}
