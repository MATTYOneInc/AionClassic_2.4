/*
 * This file is part of aion-unique <aion-unique.org>.
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

import com.aionemu.gameserver.controllers.RVController;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

import javolution.util.FastMap;

public class S_WORLD_INFO extends AionServerPacket
{
	private int actionId;
	private RVController rift;
	private FastMap<Integer, Integer> rifts;
	private int gelkmaros, inggison;
	private int objectId;
	
	public S_WORLD_INFO(FastMap<Integer, Integer> rifts) {
		this.actionId = 0;
		this.rifts = rifts;
	}
	
	public S_WORLD_INFO(boolean gelkmaros, boolean inggison) {
        this.gelkmaros = gelkmaros ? 1 : 0;
        this.inggison = inggison ? 1 : 0;
        this.actionId = 1;
    }
	
	public S_WORLD_INFO(RVController rift, boolean isMaster) {
		this.rift = rift;
		this.actionId = isMaster ? 2 : 3;
	}
	
	public S_WORLD_INFO(int objectId) {
        this.objectId = objectId;
        this.actionId = 4;
    }
	
	@Override
	protected void writeImpl(AionConnection con) {
		switch (actionId) {
			case 0:
				writeH(0x09);
				writeC(actionId);
				for (int value : rifts.values()) {
					writeD(value);
					writeD(0);
				}
			break;
			case 1:
                writeH(0x09);
                writeC(actionId);
                writeD(gelkmaros);
                writeD(inggison);
            break;
			case 2:
				writeH(0x21);
				writeC(actionId);
				writeD(rift.getOwner().getObjectId());
				writeD(rift.getMaxEntries()-rift.getUsedEntries());
				writeD(rift.getRemainTime());
				writeD(rift.getMinLevel());
				writeD(rift.getMaxLevel());
				writeF(rift.getOwner().getX());
				writeF(rift.getOwner().getY());
				writeF(rift.getOwner().getZ());
			break;
			case 3:
				writeH(0x0D);
                writeC(actionId);
                writeD(rift.getOwner().getObjectId());
                writeD(rift.getUsedEntries());
                writeD(rift.getRemainTime());
			break;
			case 4:
				writeH(0x05);
				writeC(actionId);
				writeD(objectId);
			break;
		}
	}
}