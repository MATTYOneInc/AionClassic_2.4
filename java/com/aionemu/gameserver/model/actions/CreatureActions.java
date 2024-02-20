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
package com.aionemu.gameserver.model.actions;

import com.aionemu.gameserver.model.gameobjects.Creature;

/****/
/** Author Rinzler (Encom)
/****/

public class CreatureActions
{
	public static String getName(Creature creature) {
		return creature.getName();
	}
	
	public static boolean isAlreadyDead(Creature creature) {
		return creature.getLifeStats().isAlreadyDead();
	}
	
	public static void delete(Creature creature) {
		if (creature != null) {
			creature.getController().onDelete();
		}
	}
}