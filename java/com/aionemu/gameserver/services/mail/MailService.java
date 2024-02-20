/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services.mail;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.dao.MailDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Letter;
import com.aionemu.gameserver.model.gameobjects.LetterType;
import com.aionemu.gameserver.model.gameobjects.player.Mailbox;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.templates.mail.MailMessage;
import com.aionemu.gameserver.network.aion.serverpackets.S_MAIL;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.network.aion.serverpackets.S_REMOVE_INVENTORY;
import com.aionemu.gameserver.services.AdminService;
import com.aionemu.gameserver.services.item.ItemFactory;
import com.aionemu.gameserver.services.player.PlayerMailboxState;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author kosyachok
 */
public class MailService {

	private static final Logger log = LoggerFactory.getLogger("MAIL_LOG");
	protected Queue<Player> newPlayers;

	public static final MailService getInstance() {
		return SingletonHolder.instance;
	}

	private MailService() {
		newPlayers = new ConcurrentLinkedQueue<Player>();
	}

	/**
	 * TODO split this method
	 *
	 * @param sender
	 * @param recipientName
	 * @param title
	 * @param message
	 * @param attachedItemObjId
	 * @param attachedItemCount
	 * @param attachedKinahCount
	 */
	public void sendMail(Player sender, String recipientName, String title, String message, int attachedItemObjId, int attachedItemCount,
						 int attachedKinahCount, LetterType letterType) {

		if (letterType == LetterType.BLACKCLOUD || recipientName.length() > 16)
			return;

		if (title.length() > 20)
			title = title.substring(0, 20);

		if (message.length() > 1000)
			message = message.substring(0, 1000);

		PlayerCommonData recipientCommonData = PlayerDAO.loadPlayerCommonDataByName(recipientName);

		if (recipientCommonData == null) {
			PacketSendUtility.sendPacket(sender, new S_MAIL(MailMessage.NO_SUCH_CHARACTER_NAME));
			return;
		}

		if ((recipientCommonData.getRace() != sender.getRace()) && sender.getAccessLevel() < AdminConfig.GM_LEVEL) {
			PacketSendUtility.sendPacket(sender, new S_MAIL(MailMessage.MAIL_IS_ONE_RACE_ONLY));
			return;
		}

		Player recipient = World.getInstance().findPlayer(recipientCommonData.getPlayerObjId());
		if (recipient != null) {
			if (!recipient.getMailbox().haveFreeSlots()) {
				PacketSendUtility.sendPacket(sender, new S_MAIL(MailMessage.RECIPIENT_MAILBOX_FULL));
				return;
			}
		}
		else if (recipientCommonData.getMailboxLetters() >= 98) {
			PacketSendUtility.sendPacket(sender, new S_MAIL(MailMessage.RECIPIENT_MAILBOX_FULL));
			return;
		}

		if (!validateMailSendPrice(sender, attachedKinahCount, attachedItemObjId, attachedItemCount))
			return;

		Item attachedItem = null;
		int finalAttachedKinahCount = 0;
		int finaAttachedApCount = 0;

		int kinahMailCommission = 0;
		int itemMailCommission = 0;

		Storage senderInventory = sender.getInventory();

		if (attachedItemObjId != 0 && attachedItemCount > 0) {
			Item senderItem = senderInventory.getItemByObjId(attachedItemObjId);

			if (senderItem == null) {
				return;
			}

			if (!AdminService.getInstance().canOperate(sender, null, senderItem, "mail"))
				return;

			float qualityPriceRate;
			switch (senderItem.getItemTemplate().getItemQuality()) {
				case JUNK:
				case COMMON:
					qualityPriceRate = 0.02f;
				break;
				case RARE:
					qualityPriceRate = 0.03f;
				break;
				case LEGEND:
				case UNIQUE:
					qualityPriceRate = 0.04f;
				break;
				case MYTHIC:
				case EPIC:
					qualityPriceRate = 0.05f;
				break;
				default:
					qualityPriceRate = 0.02f;
				break;
			}
			if (senderItem.getItemCount() < attachedItemCount) {
				return;// Client hack
			}
			// reuse item in case of full decrease of count
			if (senderItem.getItemCount() == attachedItemCount) {
				senderInventory.remove(senderItem);
				PacketSendUtility.sendPacket(sender, new S_REMOVE_INVENTORY(attachedItemObjId));
				attachedItem = senderItem;
			}
			else if (senderItem.getItemCount() > attachedItemCount) {
				attachedItem = ItemFactory.newItem(senderItem.getItemTemplate().getTemplateId(), attachedItemCount);
				senderInventory.decreaseItemCount(senderItem, attachedItemCount);
			}

			if (attachedItem == null)
				return;

			attachedItem.setEquipped(false);
			attachedItem.setEquipmentSlot(0);
			attachedItem.setItemLocation(StorageType.MAILBOX.getId());
			itemMailCommission = Math.round((attachedItem.getItemTemplate().getPrice() * attachedItem.getItemCount()) * qualityPriceRate);
		}

		/**
		 * Calculate kinah
		 */
		if (attachedKinahCount > 0) {
			if (senderInventory.getKinah() - attachedKinahCount >= 0) {
				finalAttachedKinahCount = attachedKinahCount;
				kinahMailCommission = Math.round(attachedKinahCount * 0.01f);
			}
		}

		int finalMailKinah = 10 + kinahMailCommission + itemMailCommission + finalAttachedKinahCount;

		if (senderInventory.getKinah() > finalMailKinah)
			senderInventory.decreaseKinah(finalMailKinah);
		else {
			AuditLogger.info(sender, "Mail kinah exploit.");
			return;
		}

		Timestamp time = new Timestamp(Calendar.getInstance().getTimeInMillis());

		Letter newLetter = new Letter(IDFactory.getInstance().nextId(), recipientCommonData.getPlayerObjId(), attachedItem,
				finalAttachedKinahCount, title, message, sender.getName(), time, true, letterType);

		// first save attached item for FK consistency
		if (attachedItem != null)
			if (!InventoryDAO.store(attachedItem, recipientCommonData.getPlayerObjId()))
				return;
		// save letter
		if (!MailDAO.storeLetter(time, newLetter))
			return;

		/**
		 * Send mail update packets
		 */
		if (recipient != null) {
			Mailbox recipientMailbox = recipient.getMailbox();
			recipientMailbox.putLetterToMailbox(newLetter);

			// packets for sender
			PacketSendUtility.sendPacket(sender, new S_MAIL(MailMessage.MAIL_SEND_SECCESS));

			// packets for recipient
			PacketSendUtility.sendPacket(recipient, new S_MAIL(recipientMailbox));
			recipientMailbox.isMailListUpdateRequired = true;

			// if recipient have opened mail list we should update it
			if (recipientMailbox.mailBoxState != 0) {
				boolean isPostman = (recipientMailbox.mailBoxState & PlayerMailboxState.EXPRESS) == PlayerMailboxState.EXPRESS;
				PacketSendUtility.sendPacket(recipient, new S_MAIL(recipient, recipientMailbox.getLetters(), isPostman));
			}

			if (letterType == LetterType.EXPRESS)
				PacketSendUtility.sendPacket(recipient, S_MESSAGE_CODE.STR_POSTMAN_NOTIFY);
		}

		if (attachedItem != null) {
		}

		/**
		 * Update loaded common data and db if player is offline
		 */
		if (!recipientCommonData.isOnline()) {
			PacketSendUtility.sendPacket(sender, new S_MAIL(MailMessage.MAIL_SEND_SECCESS));
			recipientCommonData.setMailboxLetters(recipientCommonData.getMailboxLetters() + 1);
			MailDAO.updateOfflineMailCounter(recipientCommonData);
		}
	}

	/**
	 * Read letter with specified letter id
	 *
	 * @param player
	 * @param letterId
	 */
	public void readMail(Player player, int letterId) {
		Letter letter = player.getMailbox().getLetterFromMailbox(letterId);
		if (letter == null) {
			log.warn("Cannot read mail " + player.getObjectId() + " " + letterId);
			return;
		}

		PacketSendUtility.sendPacket(player, new S_MAIL(player, letter, letter.getTimeStamp().getTime()));
		letter.setReadLetter();
	}

	/**
	 * @param player
	 * @param letterId
	 * @param attachmentType
	 */
	public void getAttachments(Player player, int letterId, int attachmentType) {
		Letter letter = player.getMailbox().getLetterFromMailbox(letterId);

		if (letter == null)
			return;

		switch (attachmentType) {
			case 0: {
				Item attachedItem = letter.getAttachedItem();
				if (attachedItem == null)
					return;
				if (player.getInventory().isFull()) {
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_FULL_INVENTORY);
					return;
				}
				player.getInventory().add(attachedItem);
				if (!InventoryDAO.store(attachedItem, player.getObjectId()))
					return;

				PacketSendUtility.sendPacket(player, new S_MAIL(letterId, attachmentType));
				letter.removeAttachedItem();
				break;
			}
			case 1: {
				player.getInventory().increaseKinah(letter.getAttachedKinah());
				PacketSendUtility.sendPacket(player, new S_MAIL(letterId, attachmentType));
				letter.removeAttachedKinah();
				break;
			}
		}
	}

	/**
	 * @param player
	 */
	public void deleteMail(Player player, int[] mailObjId) {
		Mailbox mailbox = player.getMailbox();

		for (int letterId : mailObjId) {
			mailbox.removeLetter(letterId);
			MailDAO.deleteLetter(letterId);
		}
		PacketSendUtility.sendPacket(player, new S_MAIL(mailObjId));
	}

	/**
	 * @param sender
	 * @param attachedKinahCount
	 * @param attachedItemObjId
	 * @param attachedItemCount
	 * @return
	 */
	private boolean validateMailSendPrice(Player sender, int attachedKinahCount, int attachedItemObjId, int attachedItemCount) {
		int itemMailCommission = 0;
		int kinahMailCommission = Math.round(attachedKinahCount * 0.01f);
		if (attachedItemObjId != 0) {
			Item senderItem = sender.getInventory().getItemByObjId(attachedItemObjId);
			if (senderItem == null || senderItem.getItemTemplate() == null)
				return false;
			float qualityPriceRate;
			switch (senderItem.getItemTemplate().getItemQuality()) {
				case JUNK:
				case COMMON:
					qualityPriceRate = 0.02f;
				break;
				case RARE:
					qualityPriceRate = 0.03f;
				break;
				case LEGEND:
				case UNIQUE:
					qualityPriceRate = 0.04f;
				break;
				case MYTHIC:
				case EPIC:
					qualityPriceRate = 0.05f;
				break;
				default:
					qualityPriceRate = 0.02f;
				break;
			}

			itemMailCommission = Math.round((senderItem.getItemTemplate().getPrice() * attachedItemCount) * qualityPriceRate);
		}

		int finalMailPrice = 10 + itemMailCommission + kinahMailCommission;

		if (sender.getInventory().getKinah() >= finalMailPrice)
			return true;

		return false;
	}

	/**
	 * @param player
	 */
	public void onPlayerLogin(Player player) {
		ThreadPoolManager.getInstance().schedule(new MailLoadTask(player), 5000);
	}

	public void refreshMail(Player player) {
		PacketSendUtility.sendPacket(player, new S_MAIL(player.getMailbox()));
		PacketSendUtility.sendPacket(player, new S_MAIL(player, player.getMailbox().getLetters(), false));
	}

	/**
	 * Task to load all player mail items
	 *
	 * @author ATracer
	 */
	private class MailLoadTask implements Runnable {

		private Player player;

		private MailLoadTask(Player player) {
			this.player = player;
		}

		@Override
		public void run() {
			player.setMailbox(MailDAO.loadPlayerMailbox(player));
			PacketSendUtility.sendPacket(player, new S_MAIL(player.getMailbox()));
		}

	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final MailService instance = new MailService();
	}

}
