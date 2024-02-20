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

import com.aionemu.commons.callbacks.metadata.GlobalCallback;
import com.aionemu.gameserver.configs.main.GroupConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.TeamType;
import com.aionemu.gameserver.model.team2.alliance.callback.AddPlayerToAllianceCallback;
import com.aionemu.gameserver.model.team2.alliance.callback.PlayerAllianceCreateCallback;
import com.aionemu.gameserver.model.team2.alliance.callback.PlayerAllianceDisbandCallback;
import com.aionemu.gameserver.model.team2.alliance.events.*;
import com.aionemu.gameserver.model.team2.alliance.events.AssignViceCaptainEvent.AssignType;
import com.aionemu.gameserver.model.team2.common.events.PlayerLeavedEvent.LeaveReson;
import com.aionemu.gameserver.model.team2.common.events.ShowBrandEvent;
import com.aionemu.gameserver.model.team2.common.events.TeamCommand;
import com.aionemu.gameserver.model.team2.common.events.TeamKinahDistributionEvent;
import com.aionemu.gameserver.model.team2.common.legacy.LootGroupRules;
import com.aionemu.gameserver.model.team2.common.legacy.PlayerAllianceEvent;
import com.aionemu.gameserver.network.aion.serverpackets.S_ASK;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.TimeUtil;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerAllianceService
{
    private static final Logger log = LoggerFactory.getLogger(PlayerAllianceService.class);
    private static final Map<Integer, PlayerAlliance> alliances = new ConcurrentHashMap<Integer, PlayerAlliance>();
    private static final AtomicBoolean offlineCheckStarted = new AtomicBoolean();
	
    public static final void inviteToAlliance(final Player inviter, final Player invited) {
        if (canInvite(inviter, invited)) {
            PlayerAllianceInvite invite = new PlayerAllianceInvite(inviter, invited);
            if (invited.getResponseRequester().putRequest(S_ASK.STR_MSGBOX_FORCE_INVITE_PARTY, invite)) {
                if (invited.isInGroup2()) {
                    PacketSendUtility.sendPacket(inviter, S_MESSAGE_CODE.STR_PARTY_ALLIANCE_INVITED_HIS_PARTY(invited.getName()));
                } else {
                    PacketSendUtility.sendPacket(inviter, S_MESSAGE_CODE.STR_FORCE_INVITED_HIM(invited.getName()));
                }
                PacketSendUtility.sendPacket(invited, new S_ASK(S_ASK.STR_MSGBOX_FORCE_INVITE_PARTY, 0, 0, inviter.getName()));
            }
        }
    }
	
    public static final boolean canInvite(Player inviter, Player invited) {
        return RestrictionsManager.canInviteToAlliance(inviter, invited);
    }
	
    @GlobalCallback(PlayerAllianceCreateCallback.class)
    public static final PlayerAlliance createAlliance(Player leader, Player invited, TeamType type) {
        PlayerAlliance newAlliance = new PlayerAlliance(new PlayerAllianceMember(leader), type);
        alliances.put(newAlliance.getTeamId(), newAlliance);
        addPlayer(newAlliance, leader);
        addPlayer(newAlliance, invited);
        if (offlineCheckStarted.compareAndSet(false, true)) {
            initializeOfflineCheck();
        }
        return newAlliance;
    }
	
    private static void initializeOfflineCheck() {
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new OfflinePlayerAllianceChecker(), 1000, 30 * 1000);
    }
	
    @GlobalCallback(AddPlayerToAllianceCallback.class)
    public static final void addPlayerToAlliance(PlayerAlliance alliance, Player invited) {
        alliance.addMember(new PlayerAllianceMember(invited));
    }
	
    public static final void changeGroupRules(PlayerAlliance alliance, LootGroupRules lootRules) {
        alliance.onEvent(new ChangeAllianceLootRulesEvent(alliance, lootRules));
    }
	
    public static final void onPlayerLogin(Player player) {
        for (PlayerAlliance alliance : alliances.values()) {
            PlayerAllianceMember member = alliance.getMember(player.getObjectId());
            if (member != null) {
                alliance.onEvent(new PlayerConnectedEvent(alliance, player));
            }
        }
    }
	
    public static final void onPlayerLogout(Player player) {
        PlayerAlliance alliance = player.getPlayerAlliance2();
        if (alliance != null) {
            PlayerAllianceMember member = alliance.getMember(player.getObjectId());
            member.updateLastOnlineTime();
            alliance.onEvent(new PlayerDisconnectedEvent(alliance, player));
        }
    }
	
    public static final void updateAlliance(Player player, PlayerAllianceEvent allianceEvent) {
        PlayerAlliance alliance = player.getPlayerAlliance2();
        if (alliance != null) {
            alliance.onEvent(new PlayerAllianceUpdateEvent(alliance, player, allianceEvent));
        }
    }
	
    public static final void addPlayer(PlayerAlliance alliance, Player player) {
        Preconditions.checkNotNull(alliance, "Alliance should not be null");
        alliance.onEvent(new PlayerEnteredEvent(alliance, player));
    }
	
    public static final void removePlayer(Player player) {
        PlayerAlliance alliance = player.getPlayerAlliance2();
        if (alliance != null) {
            alliance.onEvent(new PlayerAllianceLeavedEvent(alliance, player));
        }
    }
	
    public static final void banPlayer(Player bannedPlayer, Player banGiver) {
        Preconditions.checkNotNull(bannedPlayer, "Banned player should not be null");
        Preconditions.checkNotNull(banGiver, "Bangiver player should not be null");
        PlayerAlliance alliance = banGiver.getPlayerAlliance2();
        if (alliance != null) {
            PlayerAllianceMember bannedMember = alliance.getMember(bannedPlayer.getObjectId());
            if (bannedMember != null) {
                alliance.onEvent(new PlayerAllianceLeavedEvent(alliance, bannedMember.getObject(), LeaveReson.BAN, banGiver.getName()));
            } else {
                log.warn("TEAM2: banning player not in alliance {}", alliance.onlineMembers());
            }
        }
    }
	
    @GlobalCallback(PlayerAllianceDisbandCallback.class)
    public static void disband(PlayerAlliance alliance) {
        Preconditions.checkState(alliance.onlineMembers() <= 1, "Can't disband alliance with more than one online member");
        alliances.remove(alliance.getTeamId());
        alliance.onEvent(new AllianceDisbandEvent(alliance));
    }
	
    public static void changeLeader(Player player) {
        PlayerAlliance alliance = player.getPlayerAlliance2();
        if (alliance != null) {
            alliance.onEvent(new ChangeAllianceLeaderEvent(alliance, player));
        }
    }
	
    public static void changeViceCaptain(Player player, AssignType assignType) {
        PlayerAlliance alliance = player.getPlayerAlliance2();
        if (alliance != null) {
            alliance.onEvent(new AssignViceCaptainEvent(alliance, player, assignType));
        }
    }
	
    public static final PlayerAlliance searchAlliance(Integer playerObjId) {
        for (PlayerAlliance alliance : alliances.values()) {
            if (alliance.hasMember(playerObjId)) {
                return alliance;
            }
        }
        return null;
    }
	
    public static void changeMemberGroup(Player player, int firstPlayer, int secondPlayer, int allianceGroupId) {
        PlayerAlliance alliance = player.getPlayerAlliance2();
        Preconditions.checkNotNull(alliance, "Alliance should not be null for group change");
        if (alliance.isLeader(player) || alliance.isViceCaptain(player)) {
            alliance.onEvent(new ChangeMemberGroupEvent(alliance, firstPlayer, secondPlayer, allianceGroupId));
        } else {
            PacketSendUtility.sendMessage(player, "You do not have the authority for that.");
        }
    }
	
    public static void checkReady(Player player, TeamCommand eventCode) {
        PlayerAlliance alliance = player.getPlayerAlliance2();
        if (alliance != null) {
            alliance.onEvent(new CheckAllianceReadyEvent(alliance, player, eventCode));
        }
    }
	
    public static void distributeKinah(Player player, long amount) {
        PlayerAlliance alliance = player.getPlayerAlliance2();
        if (alliance != null) {
            alliance.onEvent(new TeamKinahDistributionEvent<PlayerAlliance>(alliance, player, amount));
        }
    }
	
    public static void distributeKinahInGroup(Player player, long amount) {
        PlayerAllianceGroup allianceGroup = player.getPlayerAllianceGroup2();
        if (allianceGroup != null) {
            allianceGroup.onEvent(new TeamKinahDistributionEvent<PlayerAllianceGroup>(allianceGroup, player, amount));
        }
    }
	
    public static void showBrand(Player player, int targetObjId, int brandId) {
        PlayerAlliance alliance = player.getPlayerAlliance2();
        if (alliance != null) {
            alliance.onEvent(new ShowBrandEvent<PlayerAlliance>(alliance, targetObjId, brandId));
        }
    }
	
    public static final String getServiceStatus() {
        return "Number of alliances: " + alliances.size();
    }
	
    public static class OfflinePlayerAllianceChecker implements Runnable, Predicate<PlayerAllianceMember> {
        private PlayerAlliance currentAlliance;
        @Override
        public void run() {
            for (PlayerAlliance alliance : alliances.values()) {
                currentAlliance = alliance;
                alliance.apply(this);
            }
            currentAlliance = null;
        }
		
        @Override
        public boolean apply(PlayerAllianceMember member) {
            int kickDelay = currentAlliance.getTeamType().isAutoTeam() ? 60 : GroupConfig.ALLIANCE_REMOVE_TIME;
            if (!member.isOnline() && TimeUtil.isExpired(member.getLastOnlineTime() + kickDelay * 1000)) {
                currentAlliance.onEvent(new PlayerAllianceLeavedEvent(currentAlliance, member.getObject(), LeaveReson.LEAVE_TIMEOUT));
            }
            return true;
        }
    }
}