package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_RESURRECT_INFO extends AionServerPacket
{
	private boolean hasRebirth;
	private boolean hasItem;
	private int remainingKiskTime;
	private int type = 0;
	
	public S_RESURRECT_INFO(boolean hasRebirth, boolean hasItem, int remainingKiskTime, int type) {
		this.hasRebirth = hasRebirth;
		this.hasItem = hasItem;
		this.remainingKiskTime = remainingKiskTime;
		this.type = type;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		this.writeC(this.hasRebirth ? 1 : 0);
        this.writeC(this.hasItem ? 1 : 0);
        this.writeD(this.remainingKiskTime);
        this.writeC(this.type);
        //this.writeD(32);
	}
}