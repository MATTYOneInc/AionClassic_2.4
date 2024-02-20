/*
 * This file is part of aion-lightning <aion-lightning.org>.
 *
 * aion-lightning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-lightning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.loginserver.network.gameserver.clientpackets;

import com.aionemu.loginserver.dao.AccountDAO;
import com.aionemu.loginserver.dao.PremiumDAO;
import com.aionemu.loginserver.model.Account;
import com.aionemu.loginserver.network.gameserver.GsClientPacket;

/**
 * @author xTz
 */
public class CM_ACCOUNT_TOLL_INFO extends GsClientPacket {

	private long toll;
	private long luna;

	private String accountName;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		toll = readQ();
		accountName = readS();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Account account =AccountDAO.getAccount(accountName);

		if (account != null) {
			PremiumDAO.updateToll(account.getId(), toll, 0);
		}
	}
}
