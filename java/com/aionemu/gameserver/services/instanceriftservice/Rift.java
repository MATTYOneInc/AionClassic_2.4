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
import com.aionemu.gameserver.model.instancerift.InstanceRiftStateType;

/**
 * @author Rinzler (Encom)
 */

public class Rift extends RiftInstance<InstanceRiftLocation>
{
	public Rift(InstanceRiftLocation instanceRift) {
		super(instanceRift);
	}
	
	@Override
	public void startInstanceRift() {
		getInstanceRiftLocation().setActiveInstanceRift(this);
		despawn();
		spawn(InstanceRiftStateType.OPEN);
	}
	
	@Override
	public void stopInstanceRift() {
		getInstanceRiftLocation().setActiveInstanceRift(null);
		despawn();
		spawn(InstanceRiftStateType.CLOSED);
	}
}