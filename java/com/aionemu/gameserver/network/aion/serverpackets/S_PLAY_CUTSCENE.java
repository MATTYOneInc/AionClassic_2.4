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


import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author -orz-, MrPoke
 */
public class S_PLAY_CUTSCENE extends AionServerPacket {

	private int type = 1; // if 1: CutSceneMovies else CutScenes
	private int movieId = 0;
	private int id = 0; // id scene ?
	private int restrictionId;
	private int objectId;

	public S_PLAY_CUTSCENE(int type, int movieId) {
		this.type = type;
		this.movieId = movieId;	
	}

	public S_PLAY_CUTSCENE(int type, int id, int movieId, int restrictionId) {
		this(type, movieId);
		this.id = id;
		this.restrictionId = restrictionId;
	}

	public S_PLAY_CUTSCENE(int type, int id, int movieId, int restrictionId, int objectId) {
		this(type, id, movieId, restrictionId);
		this.objectId = objectId;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeC(type);
		writeD(objectId);
		writeD(id);
		writeH(movieId);
		writeD(restrictionId);
	}
}
