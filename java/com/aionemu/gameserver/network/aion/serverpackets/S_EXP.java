package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_EXP extends AionServerPacket
{
	private long currentExp;
	private long recoverableExp;
	private long maxExp;
	private long curBoostExp = 0;
	private long maxBoostExp = 0;
	
	public S_EXP(long currentExp, long recoverableExp, long maxExp, long curBoostExp, long maxBoostExp) {
		this.currentExp = currentExp;
		this.recoverableExp = recoverableExp;
		this.maxExp = maxExp;
		this.curBoostExp = curBoostExp;
		this.maxBoostExp = maxBoostExp;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeQ(currentExp);
		writeQ(recoverableExp);
		writeQ(maxExp);
		writeQ(curBoostExp);
		writeQ(maxBoostExp);
		writeQ(0);
	}
}