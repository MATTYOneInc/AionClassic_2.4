package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.questEngine.model.QuestState;
import javolution.util.FastList;

import static com.aionemu.gameserver.dataholders.DataManager.QUEST_DATA;

public class S_LOAD_WORKINGQUEST extends AionServerPacket
{
	private FastList<QuestState> questState;
	
	public S_LOAD_WORKINGQUEST(FastList<QuestState> questState) {
		this.questState = questState;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeH(0x01);
		writeH(-questState.size() & 0xFFFF);
		for (QuestState qs: questState) {
			writeD(qs.getQuestId());
			writeC(qs.getStatus().value());
			writeD(qs.getQuestVars().getQuestVars());
			writeC(qs.getCompleteCount());
		}
		FastList.recycle(questState);
		questState = null;
	}
}