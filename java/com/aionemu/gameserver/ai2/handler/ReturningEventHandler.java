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

import com.aionemu.gameserver.ai2.AI2Logger;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.geometry.Point3D;

public class ReturningEventHandler
{
	public static void onNotAtHome(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onNotAtHome");
		} if (npcAI.setStateIfNot(AIState.RETURNING)) {
			if (npcAI.isLogging()) {
				AI2Logger.info(npcAI, "returning and restoring");
			}
			EmoteManager.emoteStartReturning(npcAI.getOwner());
		} if (npcAI.isInState(AIState.RETURNING)) {
			Npc npc = npcAI.getOwner();
			if (npc.hasWalkRoutes()) {
				WalkManager.startWalking(npcAI);
			} else {
				Point3D prevStep = npcAI.getOwner().getMoveController().recallPreviousStep();
				npcAI.getOwner().getMoveController().moveToPoint(prevStep.getX(), prevStep.getY(), prevStep.getZ());
			}
		}
	}
	
	public static void onBackHome(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "onBackHome");
		}
		npcAI.getOwner().getMoveController().clearBackSteps();
		if (npcAI.setStateIfNot(AIState.IDLE)) {
			EmoteManager.emoteStartIdling(npcAI.getOwner());
			ThinkEventHandler.thinkIdle(npcAI);
		}
		Npc npc = npcAI.getOwner();
		npc.getController().onReturnHome();
	}
}