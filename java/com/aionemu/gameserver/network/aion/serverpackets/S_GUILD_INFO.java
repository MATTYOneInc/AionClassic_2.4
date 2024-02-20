package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

import java.sql.*;
import java.util.*;

public class S_GUILD_INFO extends AionServerPacket
{
	private Legion legion;
	private int maxLenIntro = 32;
	
	public S_GUILD_INFO(Legion legion) {
		this.legion = legion;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeS(legion.getLegionName());
		writeC(legion.getLegionLevel());
		writeD(legion.getLegionRank());
		writeH(legion.getDeputyPermission());
		writeH(legion.getCenturionPermission());
		writeH(legion.getLegionaryPermission());
		writeH(legion.getVolunteerPermission());
		writeQ(legion.getContributionPoints());
		writeD(0x00);
		writeD(0x00);
		/** Get Announcements List From DB By Legion **/
		NavigableMap<Timestamp, String> announcementList = this.legion.getAnnouncementList().descendingMap();
		/** Show max 7 announcements **/
		if (!announcementList.isEmpty()) {
            int i = 0;
            for (Timestamp unixTime : announcementList.keySet()) {
                this.writeS((String)announcementList.get(unixTime));
                this.writeD((int)(unixTime.getTime() / 1000));
                this.writeH(0);
                if (++i < 7) {
					break;
				}
            }
        }
	}
}