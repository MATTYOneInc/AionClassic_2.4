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
package com.aionemu.gameserver.ai2.handler;

import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.NpcAI2;

public class SpawnEventHandler
{
	public static void onSpawn(NpcAI2 npcAI) {
        if (npcAI.setStateIfNot(AIState.IDLE) && npcAI.getOwner().getPosition().isMapRegionActive()) {
            npcAI.think();
        }
    }
	
	public static void onDespawn(NpcAI2 npcAI) {
		npcAI.setStateIfNot(AIState.DESPAWNED);
	}
	
	public static void onRespawn(NpcAI2 npcAI) {
		npcAI.getOwner().getMoveController().resetMove();
	}
}