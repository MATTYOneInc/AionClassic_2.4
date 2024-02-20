package com.aionemu.gameserver.services.mail;

import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.dao.MailDAO;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Letter;
import com.aionemu.gameserver.model.gameobjects.LetterType;
import com.aionemu.gameserver.model.gameobjects.player.Mailbox;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.mail_reward.MailRewardTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.S_MAIL;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.item.ItemFactory;
import com.aionemu.gameserver.services.player.PlayerMailboxState;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Calendar;

public class SystemMailService
{
	private static final Logger log = LoggerFactory.getLogger("SYSMAIL_LOG");

	public static final SystemMailService getInstance() {
		return SingletonHolder.instance;
	}

	private SystemMailService() {
		log.info("SystemMailService: Initialized.");
	}

	public boolean sendMail(String sender, String recipientName, String title, String message, int attachedItemObjId, long attachedItemCount, long attachedKinahCount, LetterType letterType) {
		if (attachedItemObjId != 0) {
			ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(attachedItemObjId);
			if (itemTemplate == null) {
				//log.info("[SYSMAILSERVICE] > [SenderName: " + sender + "] [RecipientName: " + recipientName + "] RETURN ITEM ID:" + itemTemplate
				//+ " ITEM COUNT " + attachedItemCount + " KINAH COUNT " + attachedKinahCount + " ITEM TEMPLATE IS MISSING ");
				return false;
			}
		} if (attachedItemCount == 0 && attachedItemObjId != 0) {
			return false;
		} if (recipientName.length() > 16) {
			//log.info("[SYSMAILSERVICE] > [SenderName: " + sender + "] [RecipientName: " + recipientName + "] ITEM RETURN" + attachedItemObjId
			//+ " ITEM COUNT " + attachedItemCount + " KINAH COUNT " + attachedKinahCount + " RECIPIENT NAME LENGTH > 16 ");
			return false;
		} if (!sender.startsWith("$$") && sender.length() > 50) {
			//log.info("[SYSMAILSERVICE] > [SenderName: " + sender + "] [RecipientName: " + recipientName + "] ITEM RETURN" + attachedItemObjId
			//+ " ITEM COUNT " + attachedItemCount + " KINAH COUNT " + attachedKinahCount + " SENDER NAME LENGTH > 16 ");
			return false;
		} if (title.length() > 20) {
			title = title.substring(0, 20);
		} if (message.length() > 1000) {
			message = message.substring(0, 1000);
		}
		PlayerCommonData recipientCommonData = PlayerDAO.loadPlayerCommonDataByName(recipientName);
		if (recipientCommonData == null) {
			//log.info("[SYSMAILSERVICE] > [RecipientName: " + recipientName + "] NO SUCH CHARACTER NAME.");
			return false;
		}
		Player recipient = World.getInstance().findPlayer(recipientCommonData.getPlayerObjId());
		if (recipient != null) {
			if (recipient.getMailbox() != null && !(recipient.getMailbox().size() < 98)) {
				//log.info("[SYSMAILSERVICE] > [SenderName: " + sender + "] [RecipientName: " + recipientCommonData.getName() + "] ITEM RETURN"
				//+ attachedItemObjId + " ITEM COUNT " + attachedItemCount + " KINAH COUNT " + attachedKinahCount + " MAILBOX FULL ");
				return false;
			}
		} else if (recipientCommonData.getMailboxLetters() > 98) {
			return false;
		}
		Item attachedItem = null;
		long finalAttachedKinahCount = 0;
		long finalAttachedApCount = 0;
		int itemId = attachedItemObjId;
		long count = attachedItemCount;
		if (itemId != 0) {
			Item senderItem = ItemFactory.newItem(itemId, count);
			if (senderItem != null) {
				senderItem.setEquipped(false);
				senderItem.setEquipmentSlot(0);
				senderItem.setItemLocation(StorageType.MAILBOX.getId());
				attachedItem = senderItem;
			}
		} if (attachedKinahCount > 0) {
			finalAttachedKinahCount = attachedKinahCount;
		}
		String finalSender = sender;
		Timestamp time = new Timestamp(Calendar.getInstance().getTimeInMillis());
		Letter newLetter = new Letter(IDFactory.getInstance().nextId(), recipientCommonData.getPlayerObjId(), attachedItem, finalAttachedKinahCount, title, message, finalSender, time, true, letterType);
		if (!MailDAO.storeLetter(time, newLetter)) {
			return false;
		} if (attachedItem != null) {
			if (!InventoryDAO.store(attachedItem, recipientCommonData.getPlayerObjId())) {
				return false;
			}
		} if (recipient != null) {
			Mailbox recipientMailbox = recipient.getMailbox();
			recipientMailbox.putLetterToMailbox(newLetter);
			PacketSendUtility.sendPacket(recipient, new S_MAIL(recipient.getMailbox()));
			recipientMailbox.isMailListUpdateRequired = true;
			if (recipientMailbox.mailBoxState != 0) {
				boolean isPostman = (recipientMailbox.mailBoxState & PlayerMailboxState.EXPRESS) == PlayerMailboxState.EXPRESS;
				PacketSendUtility.sendPacket(recipient, new S_MAIL(recipient, recipientMailbox.getLetters(), isPostman));
			} if (letterType == LetterType.EXPRESS) {
				//Express mail has arrived.
				PacketSendUtility.sendPacket(recipient, S_MESSAGE_CODE.STR_POSTMAN_NOTIFY);
			}
		} if (!recipientCommonData.isOnline()) {
			recipientCommonData.setMailboxLetters(recipientCommonData.getMailboxLetters() + 1);
			MailDAO.updateOfflineMailCounter(recipientCommonData);
		}
		return true;
	}

	public boolean sendSystemMail(String sender, String sysTitle, String sysMessage, String recipientName, Item item, long attachedKinahCount, long attachedApCount, LetterType type) {
		String title = sysTitle;
		String message = sysMessage;
		Item attachedItem = item;
		int attachedItemObjId = 0;
		long attachedItemCount = 0;
		if (attachedItem != null) {
			attachedItemObjId = attachedItem.getItemId();
			attachedItemCount = attachedItem.getItemCount();
		}
		PlayerCommonData recipientCommonData = PlayerDAO.loadPlayerCommonDataByName(recipientName);
		if (recipientCommonData == null) {
			return false;
		}
		Player recipient = World.getInstance().findPlayer(recipientCommonData.getPlayerObjId());
		if (recipient != null) {
			if (recipient.getMailbox() != null && !(recipient.getMailbox().size() < 98)) {
				return false;
			}
		} else if (recipientCommonData.getMailboxLetters() > 98) {
			return false;
		}
		Player onlineRecipient = null;
		if (recipientCommonData.getMailboxLetters() >= 98) {
			return false;
		} if (recipientCommonData.isOnline()) {
			onlineRecipient = World.getInstance().findPlayer(recipientCommonData.getPlayerObjId());
		}
		attachedItem.setEquipped(false);
		attachedItem.setEquipmentSlot(0);
		attachedItem.setItemLocation(StorageType.MAILBOX.getId());
		Timestamp time = new Timestamp(System.currentTimeMillis());
		Letter newLetter = new Letter(IDFactory.getInstance().nextId(), recipientCommonData.getPlayerObjId(), attachedItem, attachedKinahCount, title, message, sender, time, true, type);
		if (!MailDAO.storeLetter(time, newLetter)) {
			return false;
		} if (attachedItem != null) {
			if (!InventoryDAO.store(attachedItem, recipientCommonData.getPlayerObjId())) {
				return false;
			}
		} if (onlineRecipient != null) {
			Mailbox recipientMailbox = onlineRecipient.getMailbox();
			recipientMailbox.putLetterToMailbox(newLetter);
			PacketSendUtility.sendPacket(onlineRecipient, new S_MAIL(onlineRecipient, onlineRecipient.getMailbox().getLetters()));
			PacketSendUtility.sendPacket(onlineRecipient, new S_MAIL(onlineRecipient.getMailbox()));
			//Express mail has arrived.
			if (type == LetterType.EXPRESS || type == LetterType.BLACKCLOUD) {
				PacketSendUtility.sendPacket(recipient, S_MESSAGE_CODE.STR_POSTMAN_NOTIFY);
			}
		} if (!recipientCommonData.isOnline()) {
			recipientCommonData.setMailboxLetters(recipientCommonData.getMailboxLetters() + 1);
			MailDAO.updateOfflineMailCounter(recipientCommonData);
		}
		return true;
	}

	public static void sendTemplateRewardMail(final int templateId, final PlayerCommonData playerData) {
		final MailRewardTemplate reward = DataManager.MAIL_REWARD.getMailReward(templateId);
		SystemMailService.getInstance().sendMail(reward.getSender(), playerData.getName(), reward.getTitle(), reward.getBody() + "\\n\\n" + reward.getTail(), reward.getItemId(), reward.getItemCount(), reward.getKinahCount(), LetterType.NORMAL);
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		protected static final SystemMailService instance = new SystemMailService();
	}
}
