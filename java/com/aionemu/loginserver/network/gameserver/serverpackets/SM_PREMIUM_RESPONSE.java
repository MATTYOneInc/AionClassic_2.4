package com.aionemu.loginserver.network.gameserver.serverpackets;

import com.aionemu.loginserver.network.gameserver.GsConnection;
import com.aionemu.loginserver.network.gameserver.GsServerPacket;

/**
 * @author KID
 */
public class SM_PREMIUM_RESPONSE extends GsServerPacket {

	private int requestId;
	private int result;
	private long toll;;

	public SM_PREMIUM_RESPONSE(int requestId, int result, long toll) {
		this.requestId = requestId;
		this.result = result;
		this.toll = toll;
	}

	@Override
	protected void writeImpl(GsConnection con) {
		writeC(10);
		writeD(requestId);
		writeD(result);
		writeQ(toll);
	}
}
