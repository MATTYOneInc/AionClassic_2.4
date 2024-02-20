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

import com.aionemu.gameserver.model.gameobjects.Npc;

/****/
/** Author Rinzler (Encom)
/****/

public class NpcActions extends CreatureActions
{
	public static void scheduleRespawn(Npc npc) {
		npc.getController().scheduleRespawn();
	}
}