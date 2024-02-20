package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.limiteditems.LimitedItem;
import com.aionemu.gameserver.model.limiteditems.LimitedTradeNpc;
import com.aionemu.gameserver.model.templates.tradelist.TradeListTemplate;
import com.aionemu.gameserver.model.templates.tradelist.TradeListTemplate.TradeTab;
import com.aionemu.gameserver.model.templates.tradelist.TradeNpcType;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.LimitedItemTradeService;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author Dr.Nism
 */

public class S_STORE_SALE_INFO extends AionServerPacket
{
	private Integer playerObj;
	private int npcObj;
	private int npcId;
	private TradeListTemplate tlist;
	private int buyPriceModifier;
	
	public S_STORE_SALE_INFO(Player player, Npc npc, TradeListTemplate tlist, int buyPriceModifier) {
		playerObj = player.getObjectId();
		this.npcObj = npc.getObjectId();
		npcId = npc.getNpcId();
		this.tlist = tlist;
		this.buyPriceModifier = buyPriceModifier;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		if ((tlist != null) && (tlist.getNpcId() != 0) && (tlist.getCount() != 0)) {
			writeD(npcObj);
			writeC(tlist.getTradeNpcType().index());
			writeD(buyPriceModifier);
			writeC(1);
			writeC(tlist.getTradeNpcType() == TradeNpcType.NORMAL ? 1 : 0);

			writeH(tlist.getCount());
			for (TradeTab tradeTabl : tlist.getTradeTablist()) {
				writeD(tradeTabl.getId());
				Player activePlayer = con.getActivePlayer();
				if (activePlayer.isGM()) {
					PacketSendUtility.sendMessage(activePlayer, "<Tradelist Id> + " + tradeTabl.getId());
				}
			}
			int i = 0;
			LimitedTradeNpc limitedTradeNpc = null;
			if (LimitedItemTradeService.getInstance().isLimitedTradeNpc(npcId)) {
				limitedTradeNpc = LimitedItemTradeService.getInstance().getLimitedTradeNpc(npcId);
				i = limitedTradeNpc.getLimitedItems().size();
			}
			writeH(i);
			if (limitedTradeNpc != null) {
				for (LimitedItem limitedItem : limitedTradeNpc.getLimitedItems()) {
					writeD(limitedItem.getItemId());
					writeD(limitedItem.getBuyCount().get(playerObj) == null ? 0 : limitedItem.getBuyCount().get(playerObj));
					writeD(limitedItem.getSellLimit());
					writeD(limitedItem.getListId());
				}
			}
		}
	}
}