/**
 * This file is part of aion-lightning <aion-lightning.org>
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.aion.serverpackets;


import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_WIND_PATH_RESULT extends AionServerPacket {

	private int unk1;
	private int unk2;
	
	public S_WIND_PATH_RESULT(int unk1, int unk2)
	{
		this.unk1 = unk1;
		this.unk2 = unk2;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(unk1);
		writeC(unk2);
	}
}
