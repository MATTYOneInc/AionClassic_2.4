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
public class WorldWalkerFormations {

	private Map<Integer, InstanceWalkerFormations> formations;

	public WorldWalkerFormations() {
		formations = new ConcurrentHashMap<>();
	}

	/**
	 * @param instanceId
	 * @return
	 */
	protected InstanceWalkerFormations getInstanceFormations(int instanceId) {
		InstanceWalkerFormations instanceFormation = formations.get(instanceId);
		if (instanceFormation == null) {
			instanceFormation = new InstanceWalkerFormations();
			formations.put(instanceId, instanceFormation);
		}
		return instanceFormation;
	}

}
