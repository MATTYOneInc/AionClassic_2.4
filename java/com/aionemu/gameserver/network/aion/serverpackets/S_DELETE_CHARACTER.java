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
 * In this packet Server is sending response for CM_DELETE_CHARACTER.
 * 
 * @author -Nemesiss-
 */
public class S_DELETE_CHARACTER extends AionServerPacket {

	private int playerObjId;
	private int deletionTime;

	/**
	 * Constructs new <tt>SM_DELETE_CHARACTER </tt> packet
	 */
	public S_DELETE_CHARACTER(int playerObjId, int deletionTime) {
		this.playerObjId = playerObjId;
		this.deletionTime = deletionTime;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		if (playerObjId != 0) {
			writeD(0x00);// unk
			writeD(playerObjId);
			writeD(deletionTime);
		}
		else {
			writeD(0x10);// unk
			writeD(0x00);
			writeD(0x00);
		}
	}
}
