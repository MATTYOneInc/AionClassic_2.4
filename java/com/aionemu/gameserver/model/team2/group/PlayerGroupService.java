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
package com.aionemu.gameserver.model.team2.group;

import com.aionemu.commons.callbacks.metadata.GlobalCallback;
import com.aionemu.gameserver.configs.main.GroupConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.TeamType;
import com.aionemu.gameserver.model.team2.common.events.PlayerLeavedEvent.LeaveReson;
import com.aionemu.gameserver.model.team2.common.events.ShowBrandEvent;
import com.aionemu.gameserver.model.team2.common.events.TeamKinahDistributionEvent;
import com.aionemu.gameserver.model.team2.common.legacy.GroupEvent;
import com.aionemu.gameserver.model.team2.common.legacy.LootGroupRules;
import com.aionemu.gameserver.model.team2.group.callback.AddPlayerToGroupCallback;
import com.aionemu.gameserver.model.team2.group.callback.PlayerGroupCreateCallback;
import com.aionemu.gameserver.model.team2.group.callback.PlayerGroupDisbandCallback;
import com.aionemu.gameserver.model.team2.group.events.*;
import com.aionemu.gameserver.network.aion.serverpackets.S_ASK;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.TimeUtil;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerGroupService
{
    private static final Logger log = LoggerFactory.getLogger(PlayerGroupService.class);
    private static final Map<Integer, PlayerGroup> groups = new ConcurrentHashMap<Integer, PlayerGroup>();
    private static final AtomicBoolean offlineCheckStarted = new AtomicBoolean();
    private static FastMap<Integer, PlayerGroup> groupMembers;
	
    public static final void inviteToGroup(final Player inviter, final Player invited) {
        if (canInvite(inviter, invited)) {
            PlayerGroupInvite invite = new PlayerGroupInvite(inviter, invited);
            if (invited.getResponseRequester().putRequest(S_ASK.STR_PARTY_DO_YOU_ACCEPT_INVITATION, invite)) {
				PacketSendUtility.sendPacket(invited, new S_ASK(S_ASK.STR_PARTY_DO_YOU_ACCEPT_INVITATION, 0, 0, inviter.getName()));
            }
        }
    }

    public static final boolean canInvite(Player inviter, Player invited) {
        return RestrictionsManager.canInviteToGroup(inviter, invited);
    }
	
    @GlobalCallback(PlayerGroupCreateCallback.class)
    public static final PlayerGroup createGroup(Player leader, Player invited, TeamType type) {
        PlayerGroup newGroup = new PlayerGroup(new PlayerGroupMember(leader), type);
        groups.put(newGroup.getTeamId(), newGroup);
        addPlayer(newGroup, leader);
        addPlayer(newGroup, invited);
		if (offlineCheckStarted.compareAndSet(false, true)) {
            initializeOfflineCheck();
        }
        return newGroup;
    }
	
	@GlobalCallback(PlayerGroupCreateCallback.class)
    public static final PlayerGroup createGroup(Player leader) {
        PlayerGroup newGroup = new PlayerGroup(new PlayerGroupMember(leader), TeamType.GROUP);
        groups.put(newGroup.getTeamId(), newGroup);
        addPlayer(newGroup, leader);
        if (offlineCheckStarted.compareAndSet(false, true)) {
            initializeOfflineCheck();
        }
        return newGroup;
    }
	
    private static void initializeOfflineCheck() {
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new OfflinePlayerChecker(), 1000, 30 * 1000);
    }
	
    @GlobalCallback(AddPlayerToGroupCallback.class)
    public static final void addPlayerToGroup(PlayerGroup group, Player invited) {
        group.addMember(new PlayerGroupMember(invited));
    }
	
    public static final void changeGroupRules(PlayerGroup group, LootGroupRules lootRules) {
        group.onEvent(new ChangeGroupLootRulesEvent(group, lootRules));
    }
	
    public static final void onPlayerLogin(Player player) {
        for (PlayerGroup group : groups.values()) {
            PlayerGroupMember member = group.getMember(player.getObjectId());
            if (member != null) {
                group.onEvent(new PlayerConnectedEvent(group, player));
            }
        }
    }
	
    public static final void onPlayerLogout(Player player) {
        PlayerGroup group = player.getPlayerGroup2();
        if (group != null) {
            PlayerGroupMember member = group.getMember(player.getObjectId());
            member.updateLastOnlineTime();
            group.onEvent(new PlayerDisconnectedEvent(group, player));
        }
    }

    public static final void updateGroup(Player player, GroupEvent groupEvent) {
        PlayerGroup group = player.getPlayerGroup2();
        if (group != null) {
            group.onEvent(new PlayerGroupUpdateEvent(group, player, groupEvent));
        }
    }
	
    public static final void addPlayer(PlayerGroup group, Player player) {
        Preconditions.checkNotNull(group, "Group should not be null");
        group.onEvent(new PlayerEnteredEvent(group, player));
	}
	
    public static final void removePlayer(Player player) {
        PlayerGroup group = player.getPlayerGroup2();
        if (group != null) {
            group.onEvent(new PlayerGroupLeavedEvent(group, player));
        }
    }
	
    public static final void banPlayer(Player bannedPlayer, Player banGiver) {
        Preconditions.checkNotNull(bannedPlayer, "Banned player should not be null");
        Preconditions.checkNotNull(banGiver, "Bangiver player should not be null");
        PlayerGroup group = banGiver.getPlayerGroup2();
        if (group != null) {
            if (group.hasMember(bannedPlayer.getObjectId())) {
                group.onEvent(new PlayerGroupLeavedEvent(group, bannedPlayer, LeaveReson.BAN, banGiver.getName()));
            } else {
                log.warn("TEAM2: banning player not in group {}", group.onlineMembers());
            }
        }
    }
	
    @GlobalCallback(PlayerGroupDisbandCallback.class)
    public static void disband(PlayerGroup group) {
        Preconditions.checkState(group.onlineMembers() <= 1, "Can't disband group with more than one online member");
        groups.remove(group.getTeamId());
        group.onEvent(new GroupDisbandEvent(group));
    }
	
    public static void distributeKinah(Player player, long kinah) {
        PlayerGroup group = player.getPlayerGroup2();
        if (group != null) {
            group.onEvent(new TeamKinahDistributionEvent<PlayerGroup>(group, player, kinah));
        }
    }
	
    public static void showBrand(Player player, int targetObjId, int brandId) {
        PlayerGroup group = player.getPlayerGroup2();
        if (group != null) {
            group.onEvent(new ShowBrandEvent<PlayerGroup>(group, targetObjId, brandId));
        }
    }
	
    public static void changeLeader(Player player) {
        PlayerGroup group = player.getPlayerGroup2();
        if (group != null) {
            group.onEvent(new ChangeGroupLeaderEvent(group, player));
        }
    }
	
    public static void startMentoring(Player player) {
        PlayerGroup group = player.getPlayerGroup2();
        if (group != null) {
            group.onEvent(new PlayerStartMentoringEvent(group, player));
        }
    }
	
    public static void stopMentoring(Player player) {
        PlayerGroup group = player.getPlayerGroup2();
        if (group != null) {
            group.onEvent(new PlayerGroupStopMentoringEvent(group, player));
        }
    }
	
    public static final void cleanup() {
        log.info(getServiceStatus());
        groups.clear();
    }
	
    public static final String getServiceStatus() {
        return "Number of groups: " + groups.size();
    }
	
    public static final PlayerGroup searchGroup(Integer playerObjId) {
        for (PlayerGroup group : groups.values()) {
            if (group.hasMember(playerObjId)) {
                return group;
            }
        }
        return null;
    }
	
    public static class OfflinePlayerChecker implements Runnable, Predicate<PlayerGroupMember> {
        private PlayerGroup currentGroup;
        @Override
        public void run() {
            for (PlayerGroup group : groups.values()) {
                currentGroup = group;
                group.apply(this);
            }
            currentGroup = null;
        }
		
        @Override
        public boolean apply(PlayerGroupMember member) {
            if (!member.isOnline() && TimeUtil.isExpired(member.getLastOnlineTime() + GroupConfig.GROUP_REMOVE_TIME * 1000)) {
                currentGroup.onEvent(new PlayerGroupLeavedEvent(currentGroup, member.getObject()));
            }
            return true;
        }
    }
	
    public static void addGroupMemberToCache(Player player) {
        if (!groupMembers.containsKey(player.getObjectId())) {
            groupMembers.put(player.getObjectId(), player.getPlayerGroup2());
        }
    }
}