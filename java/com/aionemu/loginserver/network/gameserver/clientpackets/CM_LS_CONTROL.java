package com.aionemu.loginserver.network.gameserver.clientpackets;

import com.aionemu.loginserver.dao.AccountDAO;
import com.aionemu.loginserver.model.Account;
import com.aionemu.loginserver.network.gameserver.GsClientPacket;
import com.aionemu.loginserver.network.gameserver.serverpackets.SM_LS_CONTROL_RESPONSE;

import java.sql.Timestamp;

public class CM_LS_CONTROL extends GsClientPacket
{
	private String accountName;
	private int param;
	private int type;
	private String playerName;
	private String adminName;
	private boolean result;
	private long expireVip;

	@Override
	protected void readImpl() {
		type = readC();
		adminName = readS();
		accountName = readS();
		playerName = readS();
		param = readC();
		expireVip = readQ();
	}

	@Override
	protected void runImpl() {
		Account account = AccountDAO.getAccount(accountName);
		switch (type) {
			case 1:
				account.setAccessLevel((byte) param);
			break;
			case 2:
				account.setMembership((byte) param);
				account.setMembershipExpire(new Timestamp(expireVip));
			break;
		}
		result = AccountDAO.updateAccount(account);
		sendPacket(new SM_LS_CONTROL_RESPONSE(type, result, playerName, account.getId(), param, adminName, expireVip));
	}
}
