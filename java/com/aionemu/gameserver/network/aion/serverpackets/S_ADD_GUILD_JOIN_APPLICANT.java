package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.team.legion.LegionJoinRequest;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_ADD_GUILD_JOIN_APPLICANT extends AionServerPacket
{
    private LegionJoinRequest ljr;

    public S_ADD_GUILD_JOIN_APPLICANT(LegionJoinRequest ljr) {
        this.ljr = ljr;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeD(ljr.getPlayerId());
        writeS(ljr.getPlayerName());
        writeC(ljr.getPlayerClass());
        writeC(ljr.getGenderId());
        writeH(ljr.getLevel());
        writeS(ljr.getMsg());
        writeD((int) ljr.getDate().getTime());
    }
}