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

import com.aionemu.gameserver.model.AbyssRankingResult;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author zdead, LokiReborn
 */
public class S_ABYSS_GUILD_INFOS extends AionServerPacket {

	private List<AbyssRankingResult> data;
	private Race race;
	private int updateTime;
	private int sendData = 0;
	
	public S_ABYSS_GUILD_INFOS(int updateTime, ArrayList<AbyssRankingResult> data, Race race) {
		this.updateTime = updateTime;
		this.data = data;
		this.race = race;
		this.sendData = 1;
	}

	public S_ABYSS_GUILD_INFOS(int updateTime, Race race) {
		this.updateTime = updateTime;
		this.data = Collections.emptyList();
		this.race = race;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(race.getRaceId());// 0:Elyos 1:Asmo
		writeD(updateTime);// Date
		writeD(sendData);// 0:Nothing 1:Update Table
		writeD(sendData);// 0:Nothing 1:Update Table
		writeH(data.size());// list size
		for (AbyssRankingResult rs : data) {
			writeD(rs.getRankPos());// Current Rank
			writeD((rs.getOldRankPos() == 0) ? 76 : rs.getOldRankPos());// Old Rank
			writeD(rs.getLegionId());// Legion Id
			writeD(race.getRaceId());// 0:Elyos 1:Asmo
			writeC(rs.getLegionLevel());// Legion Level
			writeD(rs.getLegionMembers());// Legion Members
			writeQ(rs.getLegionCP());// Contribution Points
			writeS(rs.getLegionName(), 82);// Legion Name
		}
	}
}
