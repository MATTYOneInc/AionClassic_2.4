/*
 *  Aion Classic Emu based on Aion Encom Source Files
 *
 *  ENCOM Team based on Aion-Lighting Open Source
 *  All Copyrights : "Data/Copyrights/AEmu-Copyrights.text
 *
 *  iMPERIVM.FUN - AION DEVELOPMENT FORUM
 *  Forum: <http://https://imperivm.fun/>
 *
 */
package com.aionemu.gameserver.network.aion;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Letter;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.iteminfo.ItemInfoBlob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * 
 * @rework Ranastic
 *
 */
public abstract class MailServicePacket extends AionServerPacket
{
	private static final Logger log = LoggerFactory.getLogger(MailServicePacket.class);
	protected Player player;

	public MailServicePacket(Player player) {
		this.player = player;
	}

	protected void writeLettersList(Collection<Letter> letters, Player player, boolean isPostman, int showCount) {
		writeD(player.getObjectId());
		writeC(0);
		writeH(isPostman ? -showCount : -letters.size());
		for (Letter letter : letters) {
			if (isPostman) {
				if (!letter.isExpress())
					continue;
				else if (!letter.isUnread())
					continue;
			}
			writeD(letter.getObjectId());
			writeS(letter.getSenderName());
			writeS(letter.getTitle());
			writeC(letter.isUnread() ? 0 : 1);
			writeD((int) (letter.getTimeStamp().getTime() / 1000));
			if (letter.getAttachedItem() != null) {
				writeD(letter.getAttachedItem().getObjectId());
			} else {
				writeD(0);
			}
			writeQ(letter.getAttachedKinah());
			writeQ(0);
			writeC(letter.getLetterType().getId());
		}
	}

	protected void writeMailMessage(int messageId) {
		writeC(messageId);
	}

	protected void writeMailboxState(int totalCount, int unreadCount, int expressCount, int blackCloudCount) {
		writeH(totalCount);
		writeH(unreadCount);
		writeH(expressCount);
		writeH(blackCloudCount);
	}

	protected void writeLetterRead(Letter letter, long time, int totalCount, int unreadCount, int expressCount, int blackCloudCount) {
		writeD(letter.getRecipientId());
		writeD(totalCount + unreadCount * 0x10000);
		writeD(expressCount + blackCloudCount);
		writeD(letter.getObjectId());
		writeD(letter.getRecipientId());
		writeS(letter.getSenderName());
		writeS(letter.getTitle());
		writeS(letter.getMessage());
		Item item = letter.getAttachedItem();
		if (item != null) {
			ItemTemplate itemTemplate = item.getItemTemplate();
			writeD(item.getObjectId());
			writeD(itemTemplate.getTemplateId());
			writeD(1);
			writeD(0);
			writeNameId(itemTemplate.getNameId());
			ItemInfoBlob itemInfoBlob = ItemInfoBlob.getFullBlob(player, item);
			itemInfoBlob.writeMe(getBuf());
		} else {
			writeQ(0);
			writeQ(0);
			writeD(0);
		}
		writeQ((int) letter.getAttachedKinah());
		writeQ(0);
		writeC(0);
		writeD((int) (time / 1000));
		writeC(letter.getLetterType().getId());
	}

	protected void writeLetterState(int letterId, int attachmentType) {
		writeD(letterId);
		writeC(attachmentType);
		writeC(1);
	}

	protected void writeLetterDelete(int totalCount, int unreadCount, int expressCount, int blackCloudCount, int... letterIds) {
		writeD(totalCount + unreadCount * 0x10000);
		writeD(expressCount + blackCloudCount);
		writeH(letterIds.length);
		for (int letterId : letterIds) {
			writeD(letterId);
		}
	}
}