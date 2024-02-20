package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_ACTION;
import com.aionemu.gameserver.utils.PacketSendUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_SUMMON_EMOTION extends AionClientPacket
{
	private static final Logger log = LoggerFactory.getLogger(CM_SUMMON_EMOTION.class);
	
	@SuppressWarnings("unused")
	private int objId;
	
	private int emotionTypeId;
	
	public CM_SUMMON_EMOTION(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		objId = readD();
		emotionTypeId = readC();
	}
	
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		EmotionType emotionType = EmotionType.getEmotionTypeById(emotionTypeId);
		if (emotionType == EmotionType.UNK) {
			//log.error("Unknown emotion type? 0x" + Integer.toHexString(emotionTypeId).toUpperCase());
		}
		Summon summon = player.getSummon();
		if (summon == null) {
			return;
		} switch (emotionType) {
			case FLY:
			case LAND:
				PacketSendUtility.broadcastPacket(summon, new S_ACTION(summon, EmotionType.START_EMOTE2));
				PacketSendUtility.broadcastPacket(summon, new S_ACTION(summon, emotionType));
			break;
			case ATTACKMODE:
				summon.setState(CreatureState.WEAPON_EQUIPPED);
				PacketSendUtility.broadcastPacket(summon, new S_ACTION(summon, emotionType));
			break;
			case NEUTRALMODE:
				summon.unsetState(CreatureState.WEAPON_EQUIPPED);
				PacketSendUtility.broadcastPacket(summon, new S_ACTION(summon, emotionType));
			break;
		}
	}
}