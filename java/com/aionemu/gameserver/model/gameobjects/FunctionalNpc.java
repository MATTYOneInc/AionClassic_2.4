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
package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;

/****/
/** Author Rinzler (Encom)
/****/

public class FunctionalNpc extends SummonedObject<Creature>
{
	public FunctionalNpc(int objId, NpcController controller, SpawnTemplate spawnTemplate, NpcTemplate objectTemplate) {
		super(objId, controller, spawnTemplate, objectTemplate, (byte) 1);
	}
	
	@Override
	public boolean isEnemy(Creature creature) {
		return getCreator().isEnemy(creature);
	}
	
	@Override
	public NpcObjectType getNpcObjectType() {
		return NpcObjectType.NORMAL;
	}
}