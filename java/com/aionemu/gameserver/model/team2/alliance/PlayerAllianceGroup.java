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
package com.aionemu.gameserver.model.team2.alliance;

import com.aionemu.gameserver.model.team2.TemporaryPlayerTeam;

/**
 * @author ATracer
 */
public class PlayerAllianceGroup extends TemporaryPlayerTeam<PlayerAllianceMember> {

    private final PlayerAlliance alliance;

    public PlayerAllianceGroup(PlayerAlliance alliance, Integer objId) {
        super(objId);
        this.alliance = alliance;
    }

    @Override
    public void addMember(PlayerAllianceMember member) {
        super.addMember(member);
        member.setPlayerAllianceGroup(this);
        member.setAllianceId(getTeamId());
    }

    @Override
    public void removeMember(PlayerAllianceMember member) {
        super.removeMember(member);
        member.setPlayerAllianceGroup(null);
    }

    @Override
    public boolean isFull() {
        return size() == 6;
    }

    @Override
    public int getMinExpPlayerLevel() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getMaxExpPlayerLevel() {
        // TODO Auto-generated method stub
        return 0;
    }

    public PlayerAlliance getAlliance() {
        return alliance;
    }
}
