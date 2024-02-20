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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author Sweetkr
 */
public class S_MOVEBACK extends AionServerPacket {

	private Creature creature;
	private int objectId;
	private float x;
	private float y;
	private float z;

	public S_MOVEBACK(Creature creature, Creature target) {
		this(creature, target.getObjectId(), target.getX(), target.getY(), target.getZ());
	}
	public S_MOVEBACK(Creature creature, int objectId, float x, float y, float z) {
		this.creature = creature;
		this.objectId = objectId;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(creature.getObjectId());
		writeD(objectId);//targets objectId
		writeC(16); // unk
		writeF(x);
		writeF(y);
		writeF(z);
	}
}
