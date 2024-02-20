/*
 * This file is part of aion-unique <aion-unique.smfnew.com>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets;


import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemDeleteType;

//Author Avol

public class S_REMOVE_INVENTORY extends AionServerPacket {

	private final int itemObjectId;
	private final ItemDeleteType deleteType;

	public S_REMOVE_INVENTORY(int itemObjectId) {
		this(itemObjectId, ItemDeleteType.QUEST_REWARD);
	}
	
	public S_REMOVE_INVENTORY(int itemObjectId, ItemDeleteType deleteType) {
		this.itemObjectId = itemObjectId;
		this.deleteType = deleteType;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(itemObjectId);
		writeC(deleteType.getMask());
	}
}
