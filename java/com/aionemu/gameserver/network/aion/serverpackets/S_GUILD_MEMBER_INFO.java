/*
 *  Aion Classic Emu based on Aion Encom Source Files
 *
 *  ENCOM Team based on Aion-Lighting Open Source
 *  All Copyrights : "Data/Copyrights/AEmu-Copyrights.text
 *
 *  iMPERIVM.FUN - AION DEVELOPMENT FORUM
 *  Forum: <http://https://imperivm.fun/>
 *
 */
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.model.team.legion.LegionMemberEx;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

import java.util.List;

public class S_GUILD_MEMBER_INFO extends AionServerPacket
{
	private static final int OFFLINE = 0x00, ONLINE = 0x01;
	private boolean isFirst;
	private boolean result;
	private List<LegionMemberEx> legionMembers;
	
	public S_GUILD_MEMBER_INFO(List<LegionMemberEx> legionMembers, boolean result, boolean isFirst) {
		this.legionMembers = legionMembers;
		this.result = result;
		this.isFirst = isFirst;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		int size = legionMembers.size();
		writeC(isFirst ? 1 : 0);
		writeH(result ? size : -size);
		for (LegionMemberEx legionMember : legionMembers) {
			writeD(legionMember.getObjectId());
			writeS(legionMember.getName());
			writeC(legionMember.getPlayerClass().getClassId());
			writeD(legionMember.getLevel());
			writeC(legionMember.getRank().getRankId());
			writeD(legionMember.getWorldId());
			writeC(legionMember.isOnline() ? 1 : 0);
			writeS(legionMember.getSelfIntro());
			writeS(legionMember.getNickname());
			writeD(legionMember.getLastOnline());
			writeD(NetworkConfig.GAMESERVER_ID);
		}
	}
}