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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.questEngine.model.QuestState;
import javolution.util.FastList;

import static com.aionemu.gameserver.dataholders.DataManager.QUEST_DATA;

public class S_LOAD_FINISHEDQUEST extends AionServerPacket
{
	private FastList<QuestState> questState;
	
	public S_LOAD_FINISHEDQUEST(FastList<QuestState> questState) {
		this.questState = questState;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeH(0x01);
		writeH(-questState.size() & 0xFFFF);
		for (QuestState qs: questState) {
			writeD(qs.getQuestId());
			if(GSConfig.SERVER_COUNTRY_CODE == 0){
				writeD(qs.getCompleteCount());
			} else {
				writeC(qs.getCompleteCount());
			}
		}
		FastList.recycle(questState);
		questState = null;
	}
}