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
import com.aionemu.gameserver.model.team2.common.events.ChangeLeaderEvent;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.network.aion.serverpackets.S_PARTY_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.common.base.Predicate;

/**
 * @author ATracer
 */
public class ChangeGroupLeaderEvent extends ChangeLeaderEvent<PlayerGroup> {

    public ChangeGroupLeaderEvent(PlayerGroup team, Player eventPlayer) {
        super(team, eventPlayer);
    }

    public ChangeGroupLeaderEvent(PlayerGroup team) {
        super(team, null);
    }

    @Override
    public void handleEvent() {
        Player oldLeader = team.getLeaderObject();
        if (eventPlayer == null) {
            team.applyOnMembers(this);
        } else {
            changeLeaderTo(eventPlayer);
        }
        checkLeaderChanged(oldLeader);
    }

    @Override
    protected void changeLeaderTo(final Player player) {
        team.changeLeader(team.getMember(player.getObjectId()));
        team.applyOnMembers(new Predicate<Player>() {
            @Override
            public boolean apply(Player member) {
                PacketSendUtility.sendPacket(member, new S_PARTY_INFO(team));
                if (!player.equals(member)) {
                    PacketSendUtility.sendPacket(member, S_MESSAGE_CODE.STR_PARTY_HE_IS_NEW_LEADER(player.getName()));
                } else {
                    PacketSendUtility.sendPacket(member, S_MESSAGE_CODE.STR_PARTY_YOU_BECOME_NEW_LEADER);
                }
                return true;
            }
        });
    }
}
