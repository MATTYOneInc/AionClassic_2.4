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
import com.aionemu.gameserver.model.team2.common.events.PlayerStopMentoringEvent;
import com.aionemu.gameserver.model.team2.common.legacy.GroupEvent;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.network.aion.serverpackets.S_PARTY_MEMBER_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class PlayerGroupStopMentoringEvent extends PlayerStopMentoringEvent<PlayerGroup> {

    /**
     * @param group
     * @param player
     */
    public PlayerGroupStopMentoringEvent(PlayerGroup group, Player player) {
        super(group, player);
    }

    @Override
    protected void sendGroupPacketOnMentorEnd(Player member) {
        PacketSendUtility.sendPacket(member, new S_PARTY_MEMBER_INFO(team, player, GroupEvent.MOVEMENT));
    }
}
