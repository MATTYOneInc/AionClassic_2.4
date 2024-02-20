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
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.gameobjects.Npc;

public class ActivateEventHandler
{
	public static void onActivate(NpcAI2 npcAI) {
		if (npcAI.isInState(AIState.IDLE)) {
			npcAI.getOwner().updateKnownlist();
			npcAI.think();
		}
	}
	
	public static void onDeactivate(NpcAI2 npcAI) {
		if (npcAI.isInState(AIState.WALKING)) {
			WalkManager.stopWalking(npcAI);
		} else if (npcAI.isInState(AIState.TALKING)) {
            WalkManager.stopWalking(npcAI);
        }
		npcAI.think();
		Npc npc = npcAI.getOwner();
		npc.updateKnownlist();
		npc.getAggroList().clear();
		npc.getEffectController().removeAllEffects();
	}
}