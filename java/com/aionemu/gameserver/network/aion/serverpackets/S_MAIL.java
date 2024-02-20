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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.Letter;
import com.aionemu.gameserver.model.gameobjects.LetterType;
import com.aionemu.gameserver.model.gameobjects.player.Mailbox;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.mail.MailMessage;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.MailServicePacket;
import com.aionemu.gameserver.utils.collections.ListSplitter;

import java.util.Collection;

public class S_MAIL extends MailServicePacket
{
	private int serviceId;
	private Collection<Letter> letters;
	private int totalCount;
	private int unreadCount;
	private int unreadExpressCount;
	private int unreadBlackCloudCount;
	private int mailMessage;
	private Letter letter;
	private long time;
	private int letterId;
	private int[] letterIds;
	private int attachmentType;
	private boolean isExpress;
	
	public S_MAIL(Mailbox mailbox) {
		super(null);
		this.serviceId = 0;
	}
	
	public S_MAIL(MailMessage mailMessage) {
		super(null);
		this.serviceId = 1;
		this.mailMessage = mailMessage.getId();
	}
	
	public S_MAIL(Player player, Collection<Letter> letters) {
		super(player);
		this.serviceId = 2;
		this.letters = letters;
	}
	
	public S_MAIL(Player player, Collection<Letter> letters, boolean isExpress) {
		super(player);
		this.serviceId = 2;
		this.letters = letters;
		this.isExpress = isExpress;
	}
	
	public S_MAIL(Player player, Letter letter, long time) {
		super(player);
		this.serviceId = 3;
		this.letter = letter;
		this.time = time;
	}
	
	public S_MAIL(int letterId, int attachmentType) {
		super(null);
		this.serviceId = 5;
		this.letterId = letterId;
		this.attachmentType = attachmentType;
	}
	
	public S_MAIL(int[] letterIds) {
		super(null);
		this.serviceId = 6;
		this.letterIds = letterIds;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		Mailbox mailbox = con.getActivePlayer().getMailbox();
		this.totalCount = mailbox.size();
		this.unreadCount = mailbox.getUnreadCount();
		this.unreadExpressCount = mailbox.getUnreadCountByType(LetterType.EXPRESS);
		this.unreadBlackCloudCount = mailbox.getUnreadCountByType(LetterType.BLACKCLOUD);
		writeC(serviceId);
		switch (serviceId) {
			case 0:
				mailbox.isMailListUpdateRequired = true;
				writeMailboxState(totalCount, unreadCount, unreadExpressCount, unreadBlackCloudCount);
			break;
			case 1:
				writeMailMessage(mailMessage);
			break;
			case 2:
				Collection<Letter> _letters;
				if (!letters.isEmpty()) {
					ListSplitter<Letter> splittedLetters = new ListSplitter<Letter>(letters, 100); //max 98 mail!!!
					_letters = splittedLetters.getNext();
				} else {
					_letters = letters;
				}
				writeLettersList(_letters, player, isExpress, unreadExpressCount + unreadBlackCloudCount);
			break;
			case 3:
				writeLetterRead(letter, time, totalCount, unreadCount, unreadExpressCount, unreadBlackCloudCount);
			break;
			case 5:
				writeLetterState(letterId, attachmentType);
			break;
			case 6:
				mailbox.isMailListUpdateRequired = true;
				writeLetterDelete(totalCount, unreadCount, unreadExpressCount, unreadBlackCloudCount, letterIds);
			break;
		}
	}
}