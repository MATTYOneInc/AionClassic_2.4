package com.aionemu.gameserver.services;

import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.trade.Exchange;
import com.aionemu.gameserver.model.trade.ExchangeItem;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.item.ItemFactory;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.taskmanager.AbstractFIFOPeriodicTaskManager;
import com.aionemu.gameserver.taskmanager.tasks.TemporaryTradeTimeTask;
import com.aionemu.gameserver.utils.PacketSendUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExchangeService
{
	private static final Logger log = LoggerFactory.getLogger("EXCHANGE_LOG");
	private Map<Integer, Exchange> exchanges = new HashMap<Integer, Exchange>();
	private ExchangePeriodicTaskManager saveManager;

	public static final ExchangeService getInstance() {
		return SingletonHolder.instance;
	}

	private ExchangeService() {
		int DELAY_EXCHANGE_SAVE = 5000;
		saveManager = new ExchangePeriodicTaskManager(DELAY_EXCHANGE_SAVE);
	}

	public void registerExchange(Player player1, Player player2) {
		if (!validateParticipants(player1, player2)) {
			return;
		}
		player1.setTrading(true);
		player2.setTrading(true);
		exchanges.put(player1.getObjectId(), new Exchange(player1, player2));
		exchanges.put(player2.getObjectId(), new Exchange(player2, player1));
		PacketSendUtility.sendPacket(player2, new S_XCHG_START(player1.getName()));
		PacketSendUtility.sendPacket(player1, new S_XCHG_START(player2.getName()));
	}

	private boolean validateParticipants(Player player1, Player player2) {
		return RestrictionsManager.canTrade(player1) && RestrictionsManager.canTrade(player2);
	}

	private Player getCurrentParter(Player player) {
		Exchange exchange = exchanges.get(player.getObjectId());
		return exchange != null ? exchange.getTargetPlayer() : null;
	}

	private Exchange getCurrentExchange(Player player) {
		return exchanges.get(player.getObjectId());
	}

	public Exchange getCurrentParnterExchange(Player player) {
		Player partner = getCurrentParter(player);
		return partner != null ? getCurrentExchange(partner) : null;
	}

	public boolean isPlayerInExchange(Player player) {
		return getCurrentExchange(player) != null;
	}

	public void addKinah(Player activePlayer, long itemCount) {
		Exchange currentExchange = getCurrentExchange(activePlayer);
		if (currentExchange == null || currentExchange.isLocked()) {
			return;
		} if (itemCount < 1) {
			return;
		}
		long availableCount = activePlayer.getInventory().getKinah();
		availableCount -= currentExchange.getKinahCount();
		long countToAdd = availableCount > itemCount ? itemCount : availableCount;
		if (countToAdd > 0) {
			Player partner = getCurrentParter(activePlayer);
			PacketSendUtility.sendPacket(activePlayer, new S_XCHG_GOLD(countToAdd, 0));
			PacketSendUtility.sendPacket(partner, new S_XCHG_GOLD(countToAdd, 1));
			currentExchange.addKinah(countToAdd);
		}
	}

	public void addItem(Player activePlayer, int itemObjId, long itemCount) {
		Item item = activePlayer.getInventory().getItemByObjId(itemObjId);
		if (item == null) {
			return;
		}
		//Lock exchange "Medal Reward & Abyss Item"
		if (item.getItemTemplate().isMedal() || item.getItemTemplate().isAbyssItem()) {
			//You cannot register this item.
			PacketSendUtility.sendPacket(activePlayer, S_MESSAGE_CODE.STR_VENDOR_CAN_NOT_REGISTER_ITEM);
			return;
		}
		Player partner = getCurrentParter(activePlayer);
		if (partner == null) {
			return;
		} if (!TemporaryTradeTimeTask.getInstance().canTrade(item, partner.getObjectId())) {
			if (!item.isTradeable(activePlayer)) {
				return;
			}
		} if (itemCount < 1) {
			return;
		} if (itemCount > item.getItemCount()) {
			return;
		}
		Exchange currentExchange = getCurrentExchange(activePlayer);
		if (currentExchange == null) {
			return;
		} if (currentExchange.isLocked()) {
			return;
		} if (currentExchange.isExchangeListFull()) {
			return;
		} if (!AdminService.getInstance().canOperate(activePlayer, partner, item, "trade")) {
			return;
		}
		ExchangeItem exchangeItem = currentExchange.getItems().get(item.getObjectId());
		long actuallAddCount = 0;
		if (exchangeItem == null) {
			Item newItem = null;
			if (itemCount < item.getItemCount()) {
				newItem = ItemFactory.newItem(item.getItemId(), itemCount);
			} else {
				newItem = item;
			}
			exchangeItem = new ExchangeItem(itemObjId, itemCount, newItem);
			currentExchange.addItem(itemObjId, exchangeItem);
			actuallAddCount = itemCount;
		} else {
			if (item.getItemCount() == exchangeItem.getItemCount()) {
				return;
			}
			long possibleToAdd = item.getItemCount() - exchangeItem.getItemCount();
			actuallAddCount = itemCount > possibleToAdd ? possibleToAdd : itemCount;
			exchangeItem.addCount(actuallAddCount);
		}
		PacketSendUtility.sendPacket(activePlayer, new S_ADD_XCHG(0, exchangeItem.getItem(), activePlayer));
		PacketSendUtility.sendPacket(partner, new S_ADD_XCHG(1, exchangeItem.getItem(), partner));
		Item exchangedItem = exchangeItem.getItem();
	}

	public void lockExchange(Player activePlayer) {
		Exchange exchange = getCurrentExchange(activePlayer);
		if (exchange != null) {
			exchange.lock();
			Player currentParter = getCurrentParter(activePlayer);
			PacketSendUtility.sendPacket(currentParter, new S_XCHG_RESULT(3));
		}
	}

	public void cancelExchange(Player activePlayer) {
		Player currentParter = getCurrentParter(activePlayer);
		cleanupExchanges(activePlayer, currentParter);
		if (currentParter != null) {
			PacketSendUtility.sendPacket(currentParter, new S_XCHG_RESULT(1));
		}
	}

	public void confirmExchange(Player activePlayer) {
		if (activePlayer == null || !activePlayer.isOnline()) {
			return;
		}
		Exchange currentExchange = getCurrentExchange(activePlayer);
		if (currentExchange == null) {
			return;
		}
		currentExchange.confirm();
		Player currentPartner = getCurrentParter(activePlayer);
		PacketSendUtility.sendPacket(currentPartner, new S_XCHG_RESULT(2));
		if (getCurrentExchange(currentPartner).isConfirmed()) {
			performTrade(activePlayer, currentPartner);
		}
	}

	private void performTrade(Player activePlayer, Player currentPartner) {
		if (!validateExchange(activePlayer, currentPartner)) {
			cleanupExchanges(activePlayer, currentPartner);
			return;
		}
		Exchange exchange1 = getCurrentExchange(activePlayer);
		Exchange exchange2 = getCurrentExchange(currentPartner);
		cleanupExchanges(activePlayer, currentPartner);
		if (!removeItemsFromInventory(activePlayer, exchange1) || !removeItemsFromInventory(currentPartner, exchange2)) {
			return;
		}
		PacketSendUtility.sendPacket(activePlayer, new S_XCHG_RESULT(0));
		PacketSendUtility.sendPacket(currentPartner, new S_XCHG_RESULT(0));
		putItemToInventory(currentPartner, exchange1, exchange2);
		putItemToInventory(activePlayer, exchange2, exchange1);
		saveManager.add(new ExchangeOpSaveTask(exchange1.getActiveplayer().getObjectId(), exchange2.getActiveplayer().getObjectId(), exchange1.getItemsToUpdate(), exchange2.getItemsToUpdate()));
	}

	private void cleanupExchanges(Player activePlayer, Player currentPartner) {
		if (activePlayer != null) {
			exchanges.remove(activePlayer.getObjectId());
			activePlayer.setTrading(false);
		} if (currentPartner != null) {
			exchanges.remove(currentPartner.getObjectId());
			currentPartner.setTrading(false);
		}
	}

	private boolean removeItemsFromInventory(Player player, Exchange exchange) {
		Storage inventory = player.getInventory();
		for (ExchangeItem exchangeItem : exchange.getItems().values()) {
			Item item = exchangeItem.getItem();
			Item itemInInventory = inventory.getItemByObjId(exchangeItem.getItemObjId());
			if (itemInInventory == null) {
				return false;
			}
			long itemCount = exchangeItem.getItemCount();
			if (itemCount < itemInInventory.getItemCount()) {
				inventory.decreaseItemCount(itemInInventory, itemCount);
				exchange.addItemToUpdate(itemInInventory);
			} else {
				inventory.remove(itemInInventory);
				exchangeItem.setItem(itemInInventory);
				if (item.getObjectId() != exchangeItem.getItemObjId()) {
					ItemService.releaseItemId(item);
				}
				PacketSendUtility.sendPacket(player, new S_REMOVE_INVENTORY(itemInInventory.getObjectId()));
			}
		} if (!player.getInventory().tryDecreaseKinah(exchange.getKinahCount())) {
			return false;
		}
		exchange.addItemToUpdate(player.getInventory().getKinahItem());
		return true;
	}

	private boolean validateExchange(Player activePlayer, Player currentPartner) {
		Exchange exchange1 = getCurrentExchange(activePlayer);
		Exchange exchange2 = getCurrentExchange(currentPartner);
		return validateInventorySize(activePlayer, exchange2) && validateInventorySize(currentPartner, exchange1);
	}

	private boolean validateInventorySize(Player activePlayer, Exchange exchange) {
		int numberOfFreeSlots = activePlayer.getInventory().getFreeSlots();
		return numberOfFreeSlots >= exchange.getItems().size();
	}

	private void putItemToInventory(Player player, Exchange exchange1, Exchange exchange2) {
		for (ExchangeItem exchangeItem : exchange1.getItems().values()) {
			Item itemToPut = exchangeItem.getItem();
			itemToPut.setEquipmentSlot(0);
			player.getInventory().add(itemToPut);
			exchange2.addItemToUpdate(itemToPut);
		}
		long kinahToExchange = exchange1.getKinahCount();
		if (kinahToExchange > 0) {
			player.getInventory().increaseKinah(exchange1.getKinahCount());
			exchange2.addItemToUpdate(player.getInventory().getKinahItem());
		}
	}

	public static final class ExchangePeriodicTaskManager extends AbstractFIFOPeriodicTaskManager<ExchangeOpSaveTask> {
		private static final String CALLED_METHOD_NAME = "exchangeOperation()";

		public ExchangePeriodicTaskManager(int period) {
			super(period);
		}

		@Override
		protected void callTask(ExchangeOpSaveTask task) {
			task.run();
		}

		@Override
		protected String getCalledMethodName() {
			return CALLED_METHOD_NAME;
		}
	}

	public static final class ExchangeOpSaveTask implements Runnable {
		private int player1Id;
		private int player2Id;
		private List<Item> player1Items;
		private List<Item> player2Items;

		public ExchangeOpSaveTask(int player1Id, int player2Id, List<Item> player1Items, List<Item> player2Items) {
			this.player1Id = player1Id;
			this.player2Id = player2Id;
			this.player1Items = player1Items;
			this.player2Items = player2Items;
		}

		@Override
		public void run() {
			InventoryDAO.store(player1Items, player1Id);
			InventoryDAO.store(player2Items, player2Id);
		}
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		protected static final ExchangeService instance = new ExchangeService();
	}
}
