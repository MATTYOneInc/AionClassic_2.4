package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.tradelist.TradeListTemplate;
import com.aionemu.gameserver.model.templates.tradelist.TradeListTemplate.TradeTab;
import com.aionemu.gameserver.model.templates.tradelist.TradeNpcType;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Rinzler (30.03.2014)
 */

public class S_TRADE_IN extends AionServerPacket
{
	private Npc npc;
	private TradeListTemplate tlist;
	private int buyPriceModifier;
	
	public S_TRADE_IN(Npc npc, TradeListTemplate tlist, int buyPriceModifier) {
		this.npc = npc;
		this.tlist = tlist;
		this.buyPriceModifier = buyPriceModifier;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		if ((tlist != null) && (tlist.getNpcId() != 0) && (tlist.getCount() != 0)) {
			writeD(npc.getObjectId());
			writeC(tlist.getTradeNpcType().index());
			writeD(buyPriceModifier);
			writeC(1);
			writeC(tlist.getTradeNpcType() == TradeNpcType.NORMAL ? 1 : 0);
			writeH(tlist.getCount());
			for (TradeTab tradeTabl: tlist.getTradeTablist()) {
				writeD(tradeTabl.getId());
			}
		}
	}
}