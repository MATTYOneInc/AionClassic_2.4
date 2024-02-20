package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.model.gameobjects.player.emotion.Emotion;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

import java.util.Collection;

public class S_ADDREMOVE_SOCIAL extends AionServerPacket
{
	byte action;
	Collection<Emotion> emotions;
	
	public S_ADDREMOVE_SOCIAL(byte action, Collection<Emotion> emotions) {
		this.action = action;
		this.emotions = emotions;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeC(action);
		if (con.getActivePlayer().havePermission(MembershipConfig.EMOTIONS_ALL)) {
			writeH(88);
			for (int i = 0; i < 88; i++) {
				writeH(64 + i);
				writeD(0x00);
			}
		} else if (emotions == null || emotions.isEmpty()) {
			writeH(0);
		} else {
			writeH(emotions.size());
			for (Emotion emotion : emotions) {
				writeH(emotion.getId());
				writeD(emotion.getRemainingTime());
			}
		}
	}
}