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

import com.aionemu.gameserver.model.siege.ArtifactLocation;
import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.model.siege.SiegeType;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.services.SiegeService;
import javolution.util.FastList;

import java.util.ArrayList;
import java.util.Collection;

public class S_ARTIFACT_INFO extends AionServerPacket
{
	private boolean teleportStatus;
	private Collection<ArtifactLocation> locations;

	public S_ARTIFACT_INFO(Collection<ArtifactLocation> collection) {
		this.locations = collection;
	}

	public S_ARTIFACT_INFO(int loc) {
		locations = new ArrayList<ArtifactLocation>();
		locations.add(SiegeService.getInstance().getArtifact(loc));
	}

	public S_ARTIFACT_INFO(int locationId, boolean teleportStatus) {
		locations = new ArrayList<ArtifactLocation>();
		locations.add(SiegeService.getInstance().getArtifact(locationId));
		this.teleportStatus = teleportStatus;
	}


	@Override
	protected void writeImpl(AionConnection con) {
		writeH(locations.size());
		for (ArtifactLocation artifact : locations) {
			writeD(artifact.getLocationId() * 10 + 1);
			writeC(artifact.getStatus().getValue());
			writeD(0);
			//writeC(teleportStatus ? 1 : 0);
		}
	}
}