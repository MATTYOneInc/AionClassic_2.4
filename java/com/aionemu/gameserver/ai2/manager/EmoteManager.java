package com.aionemu.gameserver.ai2.manager;

import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.serverpackets.S_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class EmoteManager
{
	public static final void emoteStartAttacking(Npc owner) {
		Creature target = (Creature) owner.getTarget();
		owner.unsetState(CreatureState.WALKING);
		if (!owner.isInState(CreatureState.WEAPON_EQUIPPED)) {
			owner.setState(CreatureState.WEAPON_EQUIPPED);
			PacketSendUtility.broadcastPacket(owner, new S_ACTION(owner, EmotionType.START_EMOTE2, 0, target.getObjectId()));
			PacketSendUtility.broadcastPacket(owner, new S_ACTION(owner, EmotionType.ATTACKMODE, 0, target.getObjectId()));
		}
	}
	
	public static final void emoteStopAttacking(Npc owner) {
		owner.unsetState(CreatureState.WEAPON_EQUIPPED);
		if (owner.getTarget() != null && owner.getTarget() instanceof Player) {
			PacketSendUtility.sendPacket((Player) owner.getTarget(), S_MESSAGE_CODE.STR_UI_COMBAT_NPC_RETURN(owner.getObjectTemplate().getNameId()));
		}
	}
	
	public static final void emoteStartFollowing(Npc owner) {
		owner.unsetState(CreatureState.WALKING);
		PacketSendUtility.broadcastPacket(owner, new S_ACTION(owner, EmotionType.START_EMOTE2, 0, 0));
		PacketSendUtility.broadcastPacket(owner, new S_ACTION(owner, EmotionType.NEUTRALMODE, 0, 0));
	}
	
	public static final void emoteStartWalking(Npc owner) {
		owner.setState(CreatureState.WALKING);
		PacketSendUtility.broadcastPacket(owner, new S_ACTION(owner, EmotionType.WALK));
	}
	
	public static final void emoteStopWalking(Npc owner) {
		owner.unsetState(CreatureState.WALKING);
	}
	
	public static final void emoteStartReturning(Npc owner) {
		PacketSendUtility.broadcastPacket(owner, new S_ACTION(owner, EmotionType.START_EMOTE2, 0, 0));
		PacketSendUtility.broadcastPacket(owner, new S_ACTION(owner, EmotionType.NEUTRALMODE, 0, 0));
	}
	
	public static final void emoteStartIdling(Npc owner) {
		owner.setState(CreatureState.WALKING);
		PacketSendUtility.broadcastPacket(owner, new S_ACTION(owner, EmotionType.START_EMOTE2, 0, 0));
		PacketSendUtility.broadcastPacket(owner, new S_ACTION(owner, EmotionType.NEUTRALMODE, 0, 0));
	}
}