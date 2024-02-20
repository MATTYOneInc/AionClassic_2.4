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
package com.aionemu.gameserver.services.instanceriftservice;

import com.aionemu.gameserver.model.instancerift.InstanceRiftLocation;
import com.aionemu.gameserver.services.InstanceRiftService;

import java.util.Map;
/**
 * @author Rinzler (Encom)
 */

public class InstanceStartRunnable implements Runnable
{
	private final int id;
	
	public InstanceStartRunnable(int id) {
		this.id = id;
	}
	
	@Override
	public void run() {
		Map<Integer, InstanceRiftLocation> locations = InstanceRiftService.getInstance().getInstanceRiftLocations();
		for (InstanceRiftLocation loc : locations.values()) {
			if (loc.getId() == id) {
				InstanceRiftService.getInstance().startInstanceRift(loc.getId());
			}
		}
	}
}