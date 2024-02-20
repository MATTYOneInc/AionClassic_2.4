package com.aionemu.gameserver.ai2.handler;

import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class FollowEventHandler
{
	public static void follow(NpcAI2 npcAI, Creature creature) {
		if (npcAI.setStateIfNot(AIState.FOLLOWING)) {
			npcAI.getOwner().setTarget(creature);
			EmoteManager.emoteStartFollowing(npcAI.getOwner());
			///You start to guard the target.
			PacketSendUtility.npcSendPacketTime(npcAI.getOwner(), S_MESSAGE_CODE.STR_MSG_ESCORT_Start, 0);
		}
	}
	
	public static void creatureMoved(NpcAI2 npcAI, Creature creature) {
		if (npcAI.isInState(AIState.FOLLOWING)) {
			if (npcAI.getOwner().isTargeting(creature.getObjectId()) && !creature.getLifeStats().isAlreadyDead()) {
				checkFollowTarget(npcAI, creature);
			}
		}
	}
	
	public static void checkFollowTarget(NpcAI2 npcAI, Creature creature) {
		if (!isInRange(npcAI, creature)) {
			npcAI.onGeneralEvent(AIEventType.TARGET_TOOFAR);
		}
	}
	
	public static boolean isInRange(AbstractAI ai, VisibleObject object) {
		if (object == null) {
			return false;
		} if (object.isInInstance()) {
			return MathUtil.isIn3dRange(ai.getOwner(), object, 3);
		} else if (ai.getOwner().getLifeStats().getHpPercentage() < 100) {
			return MathUtil.isIn3dRange(ai.getOwner(), object, 3);
		} else {
			return MathUtil.isIn3dRange(ai.getOwner(), object, 3);
		}
	}
	
	public static void stopFollow(NpcAI2 npcAI, Creature creature) {
		if (npcAI.setStateIfNot(AIState.IDLE)) {
			npcAI.getOwner().setTarget(null);
			npcAI.getOwner().getMoveController().abortMove();
			npcAI.getOwner().getController().scheduleRespawn();
			npcAI.getOwner().getController().onDelete();
		}
	}
}