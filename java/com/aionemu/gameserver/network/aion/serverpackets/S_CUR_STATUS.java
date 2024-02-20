package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_CUR_STATUS extends AionServerPacket {

	byte status;

	public S_CUR_STATUS(byte status) {
		this.status = status;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeC(status);
	}
}
