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
import com.aionemu.gameserver.network.aion.serverpackets.S_PARTY_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_PARTY_MEMBER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_INSTANCE_DUNGEON_COOLTIMES;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.common.base.Predicate;

/**
 * @author ATracer
 */
public class PlayerEnteredEvent implements Predicate<Player>, TeamEvent {

    private final PlayerGroup group;
    private final Player enteredPlayer;

    public PlayerEnteredEvent(PlayerGroup group, Player enteredPlayer) {
        this.group = group;
        this.enteredPlayer = enteredPlayer;
    }

    /**
     * Entered player should not be in group yet
     */
    @Override
    public boolean checkCondition() {
        return !group.hasMember(enteredPlayer.getObjectId());
    }

    @Override
    public void handleEvent() {
        PlayerGroupService.addPlayerToGroup(group, enteredPlayer);
        PacketSendUtility.sendPacket(enteredPlayer, new S_PARTY_INFO(group));
        PacketSendUtility.sendPacket(enteredPlayer, new S_PARTY_MEMBER_INFO(group, enteredPlayer, GroupEvent.JOIN));
        PacketSendUtility.sendPacket(enteredPlayer, S_MESSAGE_CODE.STR_PARTY_ENTERED_PARTY);
        group.applyOnMembers(this);
    }

    @Override
    public boolean apply(Player player) {
        if (!player.getObjectId().equals(enteredPlayer.getObjectId())) {
            // TODO probably here JOIN event
            PacketSendUtility.sendPacket(player, new S_PARTY_MEMBER_INFO(group, enteredPlayer, GroupEvent.ENTER));
            PacketSendUtility.sendPacket(player, new S_INSTANCE_DUNGEON_COOLTIMES(enteredPlayer, false, group));
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_PARTY_HE_ENTERED_PARTY(enteredPlayer.getName()));

            PacketSendUtility.sendPacket(enteredPlayer, new S_PARTY_MEMBER_INFO(group, player, GroupEvent.ENTER));
        }
        return true;
    }
}
