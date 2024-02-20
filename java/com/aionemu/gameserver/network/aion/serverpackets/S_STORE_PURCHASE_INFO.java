package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.templates.tradelist.TradeListTemplate;
import com.aionemu.gameserver.model.templates.tradelist.TradeNpcType;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_STORE_PURCHASE_INFO extends AionServerPacket
{
	private int targetObjectId;
	private TradeListTemplate plist;
	private int sellPercentage;
	private byte action = 1;

	public S_STORE_PURCHASE_INFO(int targetObjectId, int sellPercentage) {
		this.sellPercentage = sellPercentage;
		this.targetObjectId = targetObjectId;
	}

	protected void writeImpl(AionConnection con) {
		writeD(this.targetObjectId);
		writeD(this.sellPercentage);
	}
}