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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.model.team.legion.LegionEmblem;
import com.aionemu.gameserver.model.team.legion.LegionEmblemType;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_GUILD_EMBLEM_IMG_BEGIN;
import com.aionemu.gameserver.services.legion.LegionService;

/**
 * @author Simple
 * @modified cura
 */
public class CM_LEGION_SEND_EMBLEM extends AionClientPacket {

	private int legionId;

	public CM_LEGION_SEND_EMBLEM(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		legionId = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Legion legion = LegionService.getInstance().getLegion(legionId);

		if (legion != null) {
			LegionEmblem legionEmblem = legion.getLegionEmblem();
			if (legionEmblem.getEmblemType() == LegionEmblemType.DEFAULT) {
				sendPacket(new S_GUILD_EMBLEM_IMG_BEGIN(legionId, legionEmblem.getEmblemId(), legionEmblem.getColor_r(),
					legionEmblem.getColor_g(), legionEmblem.getColor_b(), legion.getLegionName(), legionEmblem.getEmblemType(), 0));
			}
			else {
				LegionService.getInstance().sendEmblemData(getConnection().getActivePlayer(), legionEmblem, legionId,
				legion.getLegionName());
			}
		}
	}
}
