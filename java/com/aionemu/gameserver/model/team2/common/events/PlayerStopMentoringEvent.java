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
package com.aionemu.gameserver.model.team2.common.events;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.TeamMember;
import com.aionemu.gameserver.model.team2.TemporaryPlayerTeam;
import com.aionemu.gameserver.network.aion.serverpackets.S_ETC_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.common.base.Predicate;

/**
 * @author ATracer
 */
public abstract class PlayerStopMentoringEvent<T extends TemporaryPlayerTeam<? extends TeamMember<Player>>> extends AlwaysTrueTeamEvent implements Predicate<Player> {

    protected final T team;
    protected final Player player;

    public PlayerStopMentoringEvent(T team, Player player) {
        this.team = team;
        this.player = player;
    }

    @Override
    public void handleEvent() {
        player.setMentor(false);
        PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_MENTOR_END);
        team.applyOnMembers(this);
        PacketSendUtility.broadcastPacketAndReceive(player, new S_ETC_STATUS(2, player));
    }

    @Override
    public boolean apply(Player member) {
        if (!player.equals(member)) {
            PacketSendUtility.sendPacket(member, S_MESSAGE_CODE.STR_MSG_MENTOR_END_PARTYMSG(player.getName()));
        }
        sendGroupPacketOnMentorEnd(member);
        return true;
    }

    /**
     * @param member
     */
    protected abstract void sendGroupPacketOnMentorEnd(Player member);
}
