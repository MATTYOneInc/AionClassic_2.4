package com.aionemu.gameserver.network.aion.serverpackets.missing;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class SM_RECEIVE_BIDS extends AionServerPacket {

	int unk;

	public SM_RECEIVE_BIDS(int unk) {
		this.unk = unk;
	}

	protected void writeImpl(AionConnection con) {
		writeD(unk);
	}
}
