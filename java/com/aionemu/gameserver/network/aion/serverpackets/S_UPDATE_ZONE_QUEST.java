package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class S_UPDATE_ZONE_QUEST extends AionServerPacket
{
	private HashMap<Integer, Integer> nearbyQuestList;

	public S_UPDATE_ZONE_QUEST(HashMap<Integer, Integer> nearbyQuestList) {
		this.nearbyQuestList = nearbyQuestList;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		if (nearbyQuestList == null || con.getActivePlayer() == null) {
			return;
		}
		writeC(0);
		writeH(-nearbyQuestList.size() & 0xFFFF);
		for (Map.Entry<Integer, Integer> nearbyQuest : nearbyQuestList.entrySet()) {
			if (nearbyQuest.getValue() > 0) {
				writeH(nearbyQuest.getKey());
				writeH(0x02);
			} else {
				writeD(nearbyQuest.getKey());
			}
		}
	}
}