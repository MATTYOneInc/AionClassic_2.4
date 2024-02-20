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
package com.aionemu.gameserver.model.team2.group.events;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.TeamEvent;
import com.aionemu.gameserver.model.team2.common.legacy.GroupEvent;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.network.aion.serverpackets.S_PARTY_MEMBER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.common.base.Predicate;

/**
 * @author ATracer
 */
public class PlayerDisconnectedEvent implements Predicate<Player>, TeamEvent {

    private final PlayerGroup group;
    private final Player player;

    public PlayerDisconnectedEvent(PlayerGroup group, Player player) {
        this.group = group;
        this.player = player;
    }

    /**
     * Player should be in group before disconnection
     */
    @Override
    public boolean checkCondition() {
        return group.hasMember(player.getObjectId());
    }

    @Override
    public void handleEvent() {
        if (group.onlineMembers() <= 1) {
            PlayerGroupService.disband(group);
        } else {
            if (player.equals(group.getLeader().getObject())) {
                group.onEvent(new ChangeGroupLeaderEvent(group));
            }
            group.applyOnMembers(this);
        }
    }

    @Override
    public boolean apply(Player member) {
        if (!member.equals(player)) {
            PacketSendUtility.sendPacket(member, S_MESSAGE_CODE.STR_PARTY_HE_BECOME_OFFLINE(player.getName()));
            PacketSendUtility.sendPacket(member, new S_PARTY_MEMBER_INFO(group, player, GroupEvent.DISCONNECTED));
            // disconnect other group members on logout? check
            PacketSendUtility.sendPacket(player, new S_PARTY_MEMBER_INFO(group, member, GroupEvent.DISCONNECTED));
        }
        return true;
    }
}
