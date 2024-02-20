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

import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.handler.TargetEventHandler;

public class MoveEventHandler
{
	public static void onMoveValidate(NpcAI2 npcAI) {
        npcAI.getOwner().getController().onMove();
        TargetEventHandler.onTargetTooFar(npcAI);
    }
	
	public static void onMoveArrived(NpcAI2 npcAI) {
        npcAI.getOwner().getController().onMove();
        TargetEventHandler.onTargetReached(npcAI);
    }
}