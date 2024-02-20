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
package com.aionemu.gameserver.world.knownlist;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.world.MapRegion;

/**
 * @author ATracer
 */
public class NpcKnownList extends CreatureAwareKnownList {

	public NpcKnownList(VisibleObject owner) {
		super(owner);
	}

	@Override
	public void doUpdate() {
		MapRegion activeRegion = owner.getActiveRegion();
		if (activeRegion != null && activeRegion.isMapRegionActive())
			super.doUpdate();
		else
			clear();
	}
}
