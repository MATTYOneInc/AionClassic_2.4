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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author -Nemesiss-
 */

public class CM_VERSION_CHECK extends AionClientPacket
{
	private int version;
	@SuppressWarnings("unused")
	private int subversion;
	@SuppressWarnings("unused")
	private int windowsEncoding;
	@SuppressWarnings("unused")
	private int windowsVersion;
	@SuppressWarnings("unused")
	private int windowsSubVersion;
	private int unk;

	private static Logger log = LoggerFactory.getLogger(CM_VERSION_CHECK.class);
	
	public CM_VERSION_CHECK(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		version = readH();
		subversion = readH();
		windowsEncoding = readD();
		windowsVersion = readD();
		windowsSubVersion = readD();
	}
	
	@Override
	protected void runImpl() {
		sendPacket(new S_VERSION_CHECK(version));
		sendPacket(new S_SERVER_ENV());
		log.info("version : " + this.version);
	}


}