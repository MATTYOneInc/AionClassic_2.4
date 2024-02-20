/**
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aionemu.gameserver.network.loginserver.serverpackets;

import com.aionemu.gameserver.network.loginserver.LoginServerConnection;
import com.aionemu.gameserver.network.loginserver.LsServerPacket;

/**
 * @author Aionchs-Wylovech
 */
public class SM_LS_CONTROL extends LsServerPacket {

	private final String accountName;

	private final String adminName;

	private final String playerName;

	private final int param;

	private final int type;
	private final long vipexpire;

	public SM_LS_CONTROL(String accountName, String playerName, String adminName, int param, int type, long vipexpire) {
		super(0x05);
		this.accountName = accountName;
		this.param = param;
		this.playerName = playerName;
		this.adminName = adminName;
		this.type = type;
		this.vipexpire = vipexpire;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(LoginServerConnection con) {
		writeC(type);
		writeS(adminName);
		writeS(accountName);
		writeS(playerName);
		writeC(param);
		writeQ(vipexpire);
	}
}
