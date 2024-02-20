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

import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.common.events.AlwaysTrueTeamEvent;
import com.aionemu.gameserver.model.team2.league.League;
import com.aionemu.gameserver.model.team2.league.events.LeagueLeftEvent.LeaveReson;
import com.google.common.base.Predicate;

public class LeagueDisbandEvent extends AlwaysTrueTeamEvent implements Predicate<PlayerAlliance>
{
    private final League league;
	
    public LeagueDisbandEvent(League league) {
        this.league = league;
    }
	
    @Override
    public void handleEvent() {
        league.applyOnMembers(this);
    }
	
    @Override
    public boolean apply(PlayerAlliance alliance) {
        league.onEvent(new LeagueLeftEvent(league, alliance, LeaveReson.DISBAND));
        return true;
    }
}