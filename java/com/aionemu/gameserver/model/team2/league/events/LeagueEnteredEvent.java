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
package com.aionemu.gameserver.model.team2.league.events;

import com.aionemu.gameserver.model.team2.TeamEvent;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.league.League;
import com.aionemu.gameserver.model.team2.league.LeagueMember;
import com.aionemu.gameserver.model.team2.league.LeagueService;
import com.aionemu.gameserver.network.aion.serverpackets.S_ALLIANCE_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_TACTICS_SIGN;
import com.google.common.base.Predicate;

public class LeagueEnteredEvent implements Predicate<LeagueMember>, TeamEvent
{
    private final League league;
    private final PlayerAlliance invitedAlliance;
	
    public LeagueEnteredEvent(League league, PlayerAlliance alliance) {
        this.league = league;
        this.invitedAlliance = alliance;
    }
	
    @Override
    public boolean checkCondition() {
        return !league.hasMember(invitedAlliance.getObjectId());
    }
	
    @Override
    public void handleEvent() {
        LeagueService.addAllianceToLeague(league, invitedAlliance);
        league.apply(this);
    }
	
    @Override
    public boolean apply(LeagueMember member) {
        PlayerAlliance alliance = member.getObject();
        alliance.sendPacket(new S_ALLIANCE_INFO(alliance, S_ALLIANCE_INFO.UNION_ENTER, league.getLeaderObject().getLeader().getName()));
        alliance.sendPacket(new S_TACTICS_SIGN(0, 0));
        alliance.sendPacket(new S_ALLIANCE_INFO(alliance, S_ALLIANCE_INFO.UNION_ENTER, league.getLeaderObject().getLeader().getName()));
        alliance.sendPacket(new S_TACTICS_SIGN(0, 0));
        return true;
    }
}