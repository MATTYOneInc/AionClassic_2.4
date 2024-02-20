package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.tradelist.TradeListTemplate;
import com.aionemu.gameserver.model.templates.tradelist.TradeNpcType;
import com.aionemu.gameserver.model.trade.RepurchaseList;
import com.aionemu.gameserver.model.trade.TradeList;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.PrivateStoreService;
import com.aionemu.gameserver.services.RepurchaseService;
import com.aionemu.gameserver.services.TradeService;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_BUY_ITEM extends AionClientPacket
{
	private static final Logger log = LoggerFactory.getLogger(CM_BUY_ITEM.class);
	private int sellerObjId;
	private int tradeActionId;
	private int amount;
	private int itemId;
	private long count;
	private TradeList tradeList;
	private RepurchaseList repurchaseList;
	
	public CM_BUY_ITEM(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		Player player = getConnection().getActivePlayer();
		sellerObjId = readD();
		tradeActionId = readH();
		amount = readH();
		if (tradeActionId == 2) {
			repurchaseList = new RepurchaseList(sellerObjId);
		} else {
			tradeList = new TradeList(sellerObjId);
		}
		for (int i = 0; i < amount; i++) {
			itemId = readD();
			count = readQ();
			readD();//unk
			switch (tradeActionId) {
				case 0: //[Private Store]
				case 1: //[Sell To Shop]
					tradeList.addSellItem(itemId, count);
				break;
				case 2: //[Repurchase]
					repurchaseList.addRepurchaseItem(player, itemId, count);
				break;
				case 13: //[Buy From Shop]
				case 14: //[Buy From Abyss Shop]
				case 15: //[Buy From Reward Shop]
					tradeList.addBuyItem(itemId, count);
				break;
			}
		}
	}
	
	@Override
	protected void runImpl() {
		final Player player = getConnection().getActivePlayer();
        if (player == null || !player.isSpawned()) {
            return;
        } if (player.isProtectionActive()) {
            player.getController().stopProtectionActiveTask();
        } if (player.isCasting()) {
            player.getController().cancelCurrentSkill();
        } if (player.getController().isInShutdownProgress()) {
            return;
        }
		VisibleObject target = player.getKnownList().getKnownObjects().get(sellerObjId);
		if (tradeActionId != 18 && target == null) {
			return;
		} if (target instanceof Player && tradeActionId == 0) {
			Player targetPlayer = (Player) target;
			PrivateStoreService.sellStoreItem(targetPlayer, player, tradeList);
		} else if (target instanceof Npc) {
			Npc npc = (Npc) target;
			TradeListTemplate tlist = DataManager.TRADE_LIST_DATA.getTradeListTemplate(npc.getNpcId());
			TradeListTemplate purchaseTemplate = DataManager.TRADE_LIST_DATA.getPurchaseListTemplate(npc.getNpcId());
			switch (tradeActionId) {
				case 1: //[Sell To Shop]
					if (npc.getObjectTemplate().getTitleId() == 462599 ||
					    npc.getObjectTemplate().getTitleId() == 462600 ||
						npc.getObjectTemplate().getTitleId() == 462633) {
						//Sell To Shop [Purchase List AP 2.5]
						TradeService.performSellForAPToShop(player, tradeList, purchaseTemplate);
					} else {
						TradeService.performSellToShop(player, tradeList);
					}
				break;
				case 2: //[Repurchase]
					RepurchaseService.getInstance().repurchaseFromShop(player, repurchaseList);
				break;
				case 13: //[Buy From Shop]
					if (tlist != null && tlist.getTradeNpcType() == TradeNpcType.NORMAL) {
						TradeService.performBuyFromShop(npc, player, tradeList);
					}
				break;
				case 14: //[Buy From Abyss Shop]
					if (tlist != null && tlist.getTradeNpcType() == TradeNpcType.ABYSS) {
						TradeService.performBuyFromAbyssShop(npc, player, tradeList);
					}
				break;
				case 15: //[Buy From Reward Shop]
					if (tlist != null && tlist.getTradeNpcType() == TradeNpcType.REWARD) {
						TradeService.performBuyFromRewardShop(npc, player, tradeList);
					}
				break;
				default:
					log.info(String.format("Unhandle shop action unk1: %d", tradeActionId));
				break;
			}
		} if (tradeActionId == 18) { //Inventory Shop
			TradeService.performSellToShop(player, tradeList);
		}
	}
}