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
package com.aionemu.gameserver.network.aion.serverpackets;


import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * In this packet Server is sending response for CM_RESTORE_CHARACTER.
 * 
 * @author -Nemesiss-
 */
public class S_RESTORE_CHARACTER extends AionServerPacket {

	/**
	 * Character object id.
	 */
	private final int chaOid;
	/**
	 * True if player was restored.
	 */
	private final boolean success;

	/**
	 * Constructs new <tt>SM_RESTORE_CHARACTER </tt> packet
	 */
	public S_RESTORE_CHARACTER(int chaOid, boolean success) {
		this.chaOid = chaOid;
		this.success = success;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(success ? 0x00 : 0x10);// unk
		writeD(chaOid);
	}
}
