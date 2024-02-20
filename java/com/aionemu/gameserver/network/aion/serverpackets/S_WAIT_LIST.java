/*
 * This file is part of aion-unique <aion-unique.com>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets;


import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Simple
 */
public class S_WAIT_LIST extends AionServerPacket {

	private int waitingPosition; // What is the player's position in line
	private int waitingTime; // Per waiting position in seconds
	private int waitingCount; // How many are waiting in line

	private S_WAIT_LIST() {
		this.waitingPosition = 5;
		this.waitingTime = 60;
		this.waitingCount = 50;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(waitingPosition);
		writeD(waitingTime);
		writeD(waitingCount);
	}
}
