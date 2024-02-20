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
package com.aionemu.gameserver.controllers.attack;

import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * @author ATracer
 */
public class PlayerAggroList extends AggroList {

	/**
	 * @param owner
	 */
	public PlayerAggroList(Creature owner) {
		super(owner);
	}

	@Override
	protected boolean isAware(Creature creature) {
		return creature != null && !creature.getObjectId().equals(owner.getObjectId());
	}
}
