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
package com.aionemu.gameserver.world.zone.handler;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.world.zone.ZoneInstance;


/**
 * @author MrPoke
 *
 */
public interface AdvencedZoneHandler extends ZoneHandler {
	
	/**
	 * This call if creature die in zone.
	 * @param attacker
	 * @param target
	 * @return TRUE if hadle die event.
	 */
	public boolean onDie(Creature attacker, Creature target, ZoneInstance zone);
	
}
