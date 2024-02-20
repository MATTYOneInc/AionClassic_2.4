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
package com.aionemu.gameserver.services;

import com.aionemu.commons.callbacks.Callback;
import com.aionemu.commons.callbacks.util.GlobalCallbackHelper;
import com.aionemu.commons.objects.filter.ObjectFilter;

import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.autogroup.AutoGroupType;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.FindGroup;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.alliance.PlayerAlliance;
import com.aionemu.gameserver.model.team2.alliance.callback.AddPlayerToAllianceCallback;
import com.aionemu.gameserver.model.team2.alliance.callback.PlayerAllianceCreateCallback;
import com.aionemu.gameserver.model.team2.alliance.callback.PlayerAllianceDisbandCallback;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.team2.group.callback.AddPlayerToGroupCallback;
import com.aionemu.gameserver.model.team2.group.callback.PlayerGroupCreateCallback;
import com.aionemu.gameserver.model.team2.group.callback.PlayerGroupDisbandCallback;
import com.aionemu.gameserver.network.aion.serverpackets.S_MATCHMAKER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_PARTY_MATCH;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.*;
import javolution.util.FastMap;

public class FindGroupService
{
	private FastMap<Integer, FindGroup> elyosRecruitFindGroups = new FastMap().shared();
    private FastMap<Integer, FindGroup> elyosApplyFindGroups = new FastMap().shared();
    private FastMap<Integer, FindGroup> asmodianRecruitFindGroups = new FastMap().shared();
    private FastMap<Integer, FindGroup> asmodianApplyFindGroups = new FastMap().shared();
	
    private FindGroupService() {
        GlobalCallbackHelper.addCallback((Callback)new FindGroupOnAddPlayerToGroupListener());
        GlobalCallbackHelper.addCallback((Callback)new FindGroupPlayerGroupdDisbandListener());
        GlobalCallbackHelper.addCallback((Callback)new FindGroupPlayerGroupdCreateListener());
        GlobalCallbackHelper.addCallback((Callback)new FindGroupOnAddPlayerToAllianceListener());
        GlobalCallbackHelper.addCallback((Callback)new FindGroupAllianceDisbandListener());
        GlobalCallbackHelper.addCallback((Callback)new FindGroupAllianceCreateListener());
    }
	
    public static FindGroupService getInstance() {
        return SingletonHolder.instance;
    }
	
    public void addFindGroupList(Player player, int action, String message, int groupType) {
        AionObject object = null;
        object = player.isInTeam() ? player.getCurrentTeam() : player;
        FindGroup findGroup = new FindGroup(object, message, groupType);
        int objectId = object.getObjectId();
        switch (player.getRace()) {
            case ELYOS: {
                switch (action) {
                    case 2: {
                        this.elyosRecruitFindGroups.put(objectId, findGroup);
                        PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400392, new Object[0]));
                        break;
                    } case 6: {
                        this.elyosApplyFindGroups.put(objectId, findGroup);
                        PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400393, new Object[0]));
                    }
                }
                break;
            } case ASMODIANS: {
                switch (action) {
                    case 2: {
                        this.asmodianRecruitFindGroups.put(objectId, findGroup);
                        PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400392, new Object[0]));
                        break;
                    } case 6: {
                        this.asmodianApplyFindGroups.put(objectId, findGroup);
                        PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400393, new Object[0]));
                    }
                }
            }
        }
        ArrayList<FindGroup> findGroupList = new ArrayList<FindGroup>();
        findGroupList.add(findGroup);
        PacketSendUtility.sendPacket(player, new S_PARTY_MATCH(action, (int)(System.currentTimeMillis() / 1000), findGroupList));
    }
	
    public void updateFindGroupList(Player player, String message, int objectId) {
        FindGroup findGroup = null;
        switch (player.getRace()) {
            case ELYOS: {
                findGroup = (FindGroup)this.elyosRecruitFindGroups.get((Object)objectId);
                findGroup.setMessage(message);
                break;
            } case ASMODIANS: {
                findGroup = (FindGroup)this.asmodianRecruitFindGroups.get((Object)objectId);
                findGroup.setMessage(message);
            }
        }
    }
	
    public Collection<FindGroup> getFindGroups(Race race, int action) {
        switch (race) {
            case ELYOS: {
                switch (action) {
                    case 0: {
                        return this.elyosRecruitFindGroups.values();
                    } case 4: {
                        return this.elyosApplyFindGroups.values();
                    } case 10: {
                        return Collections.emptyList();
                    }
                }
                break;
            } case ASMODIANS: {
                switch (action) {
                    case 0: {
                        return this.asmodianRecruitFindGroups.values();
                    } case 4: {
                        return this.asmodianApplyFindGroups.values();
                    } case 10: {
                        return Collections.emptyList();
                    }
                }
				break;
            }
        }
        return null;
    }
	
    public void registerInstanceGroup(Player player, int action, int instanceId, String message, int minMembers, int groupType) {
        AutoGroupType agt = AutoGroupType.getAGTByMaskId(instanceId);
        if (agt != null) {
            PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(instanceId, 1, 0, player.getName()));
        }
    }
	
    public void sendFindGroups(Player player, int action) {
        PacketSendUtility.sendPacket(player, new S_PARTY_MATCH(action, (int) (System.currentTimeMillis() / 1000), this.getFindGroups(player.getRace(), action)));
    }
	
    public FindGroup removeFindGroup(final Race race, int action, int playerObjId) {
        FindGroup findGroup = null;
        switch (race) {
            case ELYOS: {
                switch (action) {
                    case 0: {
                        findGroup = (FindGroup)this.elyosRecruitFindGroups.remove((Object)playerObjId);
                        break;
                    } case 4: {
                        findGroup = (FindGroup)this.elyosApplyFindGroups.remove((Object)playerObjId);
                    }
                }
                break;
            } case ASMODIANS: {
                switch (action) {
                    case 0: {
                        findGroup = (FindGroup)this.asmodianRecruitFindGroups.remove((Object)playerObjId);
                        break;
                    } case 4: {
                        findGroup = (FindGroup)this.asmodianApplyFindGroups.remove((Object)playerObjId);
                    }
                }
            }
        } if (findGroup != null) {
            PacketSendUtility.broadcastFilteredPacket(new S_PARTY_MATCH(action + 1, playerObjId, findGroup.getUnk()), new ObjectFilter<Player>(){
                public boolean acceptObject(Player object) {
                    return race == object.getRace();
                }
            });
        }
        return findGroup;
    }
	
    public void clean() {
        this.cleanMap(this.elyosRecruitFindGroups, Race.ELYOS, 0);
        this.cleanMap(this.elyosApplyFindGroups, Race.ELYOS, 4);
        this.cleanMap(this.asmodianRecruitFindGroups, Race.ASMODIANS, 0);
        this.cleanMap(this.asmodianApplyFindGroups, Race.ASMODIANS, 4);
    }
	
    private void cleanMap(FastMap<Integer, FindGroup> map, Race race, int action) {
        for (FindGroup group : map.values()) {
            if ((long)(group.getLastUpdate() + 3600) >= System.currentTimeMillis() / 1000) {
			    this.removeFindGroup(race, action, group.getObjectId());
			}
        }
    }
	
    static class FindGroupOnAddPlayerToAllianceListener extends AddPlayerToAllianceCallback {
        FindGroupOnAddPlayerToAllianceListener() {
        }
        @Override
        public void onBeforePlayerAddToAlliance(PlayerAlliance alliance, Player player) {
            FindGroupService.getInstance().removeFindGroup(player.getRace(), 0, player.getObjectId());
            FindGroupService.getInstance().removeFindGroup(player.getRace(), 4, player.getObjectId());
        }
        @Override
        public void onAfterPlayerAddToAlliance(PlayerAlliance alliance, Player player) {
            if (alliance.isFull()) {
                FindGroupService.getInstance().removeFindGroup(alliance.getRace(), 0, alliance.getObjectId());
            }
        }
    }
	
    static class FindGroupAllianceCreateListener extends PlayerAllianceCreateCallback {
        FindGroupAllianceCreateListener() {
        }
        @Override
        public void onBeforeAllianceCreate(Player player) {
        }
        @Override
        public void onAfterAllianceCreate(Player player) {
            FindGroup inviterFindGroup = FindGroupService.getInstance().removeFindGroup(player.getRace(), 0, player.getObjectId());
            if (inviterFindGroup == null) {
                inviterFindGroup = FindGroupService.getInstance().removeFindGroup(player.getRace(), 4, player.getObjectId());
            } if (inviterFindGroup != null) {
                FindGroupService.getInstance().addFindGroupList(player, 2, inviterFindGroup.getMessage(), inviterFindGroup.getGroupType());
            }
        }
    }
	
    static class FindGroupAllianceDisbandListener extends PlayerAllianceDisbandCallback {
        FindGroupAllianceDisbandListener() {
        }
        @Override
        public void onBeforeAllianceDisband(PlayerAlliance alliance) {
            FindGroupService.getInstance().removeFindGroup(alliance.getRace(), 0, alliance.getTeamId());
        }
        @Override
        public void onAfterAllianceDisband(PlayerAlliance alliance) {
        }
    }
	
    static class FindGroupPlayerGroupdCreateListener extends PlayerGroupCreateCallback {
        FindGroupPlayerGroupdCreateListener() {
        }
        @Override
        public void onBeforeGroupCreate(Player player) {
        }
        @Override
        public void onAfterGroupCreate(Player player) {
            FindGroup inviterFindGroup = FindGroupService.getInstance().removeFindGroup(player.getRace(), 0, player.getObjectId());
            if (inviterFindGroup == null) {
                inviterFindGroup = FindGroupService.getInstance().removeFindGroup(player.getRace(), 4, player.getObjectId());
            } if (inviterFindGroup != null) {
                FindGroupService.getInstance().addFindGroupList(player, 2, inviterFindGroup.getMessage(), inviterFindGroup.getGroupType());
            }
        }
    }
	
    static class FindGroupPlayerGroupdDisbandListener extends PlayerGroupDisbandCallback {
        FindGroupPlayerGroupdDisbandListener() {
        }
        @Override
        public void onBeforeGroupDisband(PlayerGroup group) {
            FindGroupService.getInstance().removeFindGroup(group.getRace(), 0, group.getTeamId());
        }
        @Override
        public void onAfterGroupDisband(PlayerGroup group) {
        }
    }
	
    static class FindGroupOnAddPlayerToGroupListener extends AddPlayerToGroupCallback {
        FindGroupOnAddPlayerToGroupListener() {
        }
        @Override
        public void onBeforePlayerAddToGroup(PlayerGroup group, Player player) {
            FindGroupService.getInstance().removeFindGroup(player.getRace(), 0, player.getObjectId());
            FindGroupService.getInstance().removeFindGroup(player.getRace(), 4, player.getObjectId());
        }
        @Override
        public void onAfterPlayerAddToGroup(PlayerGroup group, Player player) {
            if (group.isFull()) {
                FindGroupService.getInstance().removeFindGroup(group.getRace(), 0, group.getObjectId());
            }
        }
    }
	
    private static class SingletonHolder {
        protected static final FindGroupService instance = new FindGroupService();
        private SingletonHolder() {
        }
    }
}