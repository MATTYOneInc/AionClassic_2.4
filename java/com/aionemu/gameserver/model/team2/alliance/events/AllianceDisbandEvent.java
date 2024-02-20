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
package com.aionemu.gameserver.model.team2.alliance.events;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.common.events.AlwaysTrueTeamEvent;
import com.aionemu.gameserver.model.team2.common.events.PlayerLeavedEvent.LeaveReson;
import com.google.common.base.Predicate;

public class AllianceDisbandEvent extends AlwaysTrueTeamEvent implements Predicate<Player>
{
    private final PlayerAlliance alliance;
	
    public AllianceDisbandEvent(PlayerAlliance alliance) {
        this.alliance = alliance;
    }
	
    @Override
    public void handleEvent() {
        alliance.applyOnMembers(this);
    }
	
    @Override
    public boolean apply(Player player) {
        alliance.onEvent(new PlayerAllianceLeavedEvent(alliance, player, LeaveReson.DISBAND));
        return true;
    }
}