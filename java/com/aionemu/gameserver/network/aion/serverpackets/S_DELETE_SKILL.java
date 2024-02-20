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

package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_DELETE_SKILL extends AionServerPacket
{
	private int skillId;
	private int skillLevel;
	private boolean isStigma;
	
	public S_DELETE_SKILL(int skillId, int skillLevel, boolean isStigma) {
		this.skillId = skillId;
		this.skillLevel = skillLevel;
		this.isStigma = isStigma;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeH(skillId);
		if (skillId >= 30001 && skillId <= 30003 || skillId >= 40001 && skillId <= 40009) {
			writeC(0);
			writeC(0);
		} else if (isStigma) {
			writeC(skillLevel);
			writeC(1);
		} else {
			writeC(skillLevel);
		}
	}
}