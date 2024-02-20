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


import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

public class S_QUEST extends AionServerPacket
{
	protected int questId;
	private int status;
	private int step;
	protected int action;
	private int timer;
	private int sharerId;
	@SuppressWarnings("unused")
	private boolean unk;
	
	public S_QUEST(int questId, int status, int step) {
		this.action = 1;
		this.questId = questId;
		this.status = status;
		this.step = step;
	}
	
	public S_QUEST(int questId, QuestStatus status, int step) {
		this.action = 2;
		this.questId = questId;
		this.status = status.value();
		this.step = step;
	}
	
	public S_QUEST(int questId) {
		this.action = 3;
		this.questId = questId;
	}
	
	public S_QUEST(int questId, int timer) {
		this.action = 4;
		this.questId = questId;
		this.timer = timer;
		this.step = 0;
	}
	
	public S_QUEST(int questId, int sharerId, boolean unk) {
		this.action = 5;
		this.questId = questId;
		this.sharerId = sharerId;
		this.unk = unk;
	}
	
	public S_QUEST(int questId, boolean fake) {
		this.action = 6;
		this.questId = questId;
		this.timer = 0;
		this.step = 0;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeC(action);
		writeD(questId);
		switch (action) {
			case 1:
				writeC(status);
				writeC(0x0);
				writeD(step);
				writeH(0);
				writeC(0);
			break;
			case 2:
				writeC(status);
				writeC(0x0);
				writeD(step);
				writeH(0);
			break;
			case 3:
				writeD(0);
			break;
			case 4:
				writeD(timer);
				writeC(0x01);
				writeH(0x0);
				writeC(0x01);
			break;
			case 5:
				writeD(this.sharerId);
				writeD(0);
			break;
			case 6:
				writeH(0x01);
				writeH(0x0);
			break;	
		}
	}
}