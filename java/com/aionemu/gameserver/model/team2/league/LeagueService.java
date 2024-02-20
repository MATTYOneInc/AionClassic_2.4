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
package com.aionemu.gameserver.model.team2.league;

import com.aionemu.commons.callbacks.util.GlobalCallbackHelper;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.alliance.callback.PlayerAllianceDisbandCallback;
import com.aionemu.gameserver.model.team2.league.events.LeagueDisbandEvent;
import com.aionemu.gameserver.model.team2.league.events.LeagueEnteredEvent;
import com.aionemu.gameserver.model.team2.league.events.LeagueInvite;
import com.aionemu.gameserver.model.team2.league.events.LeagueLeftEvent;
import com.aionemu.gameserver.network.aion.serverpackets.S_ASK;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LeagueService
{
    private static final Logger log = LoggerFactory.getLogger(LeagueService.class);
    private static final Map<Integer, League> leagues = new ConcurrentHashMap<Integer, League>();
	
    static {
        GlobalCallbackHelper.addCallback(new AllianceDisbandListener());
    }
	
    public static final void inviteToLeague(final Player inviter, final Player invited) {
        if (canInvite(inviter, invited)) {
            LeagueInvite invite = new LeagueInvite(inviter, invited);
            if (invited.getResponseRequester().putRequest(S_ASK.STR_MSGBOX_UNION_INVITE_ME, invite)) {
                if (invited.isInAlliance2()) {
					PacketSendUtility.sendPacket(inviter, S_MESSAGE_CODE.STR_UNION_INVITE_HIM(invited.getName(), inviter.getName()));
				}
				PacketSendUtility.sendPacket(invited, new S_ASK(S_ASK.STR_MSGBOX_UNION_INVITE_ME, 0, 0, inviter.getName()));
            }
        }
    }
	
    public static final boolean canInvite(Player inviter, Player invited) {
		return inviter.isInAlliance2() && invited.isInAlliance2() && inviter.getPlayerAlliance2().isLeader(inviter);
    }
	
    public static final League createLeague(Player inviter, Player invited) {
        PlayerAlliance alliance = inviter.getPlayerAlliance2();
        Preconditions.checkNotNull(alliance, "League can not be null");
        League newLeague = new League(new LeagueMember(alliance, 0));
        leagues.put(newLeague.getTeamId(), newLeague);
        addAlliance(newLeague, alliance);
        return newLeague;
    }
	
    public static final void addAlliance(League league, PlayerAlliance alliance) {
        Preconditions.checkNotNull(league, "League should not be null");
        league.onEvent(new LeagueEnteredEvent(league, alliance));
    }
	
    public static final void addAllianceToLeague(League league, PlayerAlliance alliance) {
        league.addMember(new LeagueMember(alliance, league.size()));
    }
	
    public static final void removeAlliance(PlayerAlliance alliance) {
        if (alliance != null) {
            League league = alliance.getLeague();
            Preconditions.checkNotNull(league, "League should not be null");
            league.onEvent(new LeagueLeftEvent(league, alliance));
        }
    }
	
    public static final void expelAlliance(Player expelledPlayer, Player expelGiver) {
        Preconditions.checkNotNull(expelledPlayer, "Expelled player should not be null");
        Preconditions.checkNotNull(expelGiver, "ExpelGiver player should not be null");
        Preconditions.checkArgument(expelGiver.isInLeague(), "Expelled player should be in league");
        Preconditions.checkArgument(expelledPlayer.isInLeague(), "ExpelGiver should be in league");
        Preconditions.checkArgument(expelGiver.getPlayerAlliance2().getLeague().isLeader(expelGiver.getPlayerAlliance2()), "ExpelGiver alliance should be the leader of league");
        Preconditions.checkArgument(expelGiver.getPlayerAlliance2().isLeader(expelGiver), "ExpelGiver should be the leader of alliance");
        PlayerAlliance alliance = expelGiver.getPlayerAlliance2();
        League league = alliance.getLeague();
        league.onEvent(new LeagueLeftEvent(league, expelledPlayer.getPlayerAlliance2(), LeagueLeftEvent.LeaveReson.EXPEL));
    }
	
    public static void disband(League league) {
        Preconditions.checkState(league.onlineMembers() <= 1, "Can't disband league with more than one online member");
        leagues.remove(league.getTeamId());
        league.onEvent(new LeagueDisbandEvent(league));
    }
	
    static class AllianceDisbandListener extends PlayerAllianceDisbandCallback {
        @Override
        public void onBeforeAllianceDisband(PlayerAlliance alliance) {
        }
		
        @Override
        public void onAfterAllianceDisband(PlayerAlliance alliance) {
            try {
                for (League league : leagues.values()) {
                    if (league.hasMember(alliance.getTeamId())) {
                        league.onEvent(new LeagueLeftEvent(league, alliance));
                    }
                }
            } catch (Throwable t) {
                //log.error("Error during alliance disband listen", t);
            }
        }
    }
}
