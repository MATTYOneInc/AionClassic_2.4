/*
 * This file is part of aion-unique <www.aion-unique.org>.
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


import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Nemiroff Date: 17.02.2010
 */
//TODO Rename
public class S_ETC_STATUS extends AionServerPacket {

	private Player player;
	private int action;

	public S_ETC_STATUS(int action, Player player) {
		this.action = action;
		this.player = player;
	}


	@Override
	protected void writeImpl(AionConnection con) {
		writeC(action);
		writeD(player.getObjectId());
		switch(action){
			case 0:
				writeD(player.getAbyssRank().getRank().getId());
				break;
			case 1:
			case 2:
				if (player.isMentor())
					writeD(1);
				else
					writeD(0);
				break;
		}
	}
}
