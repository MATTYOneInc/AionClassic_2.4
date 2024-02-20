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
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceMember;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.alliance.events.AssignViceCaptainEvent.AssignType;
import com.aionemu.gameserver.model.team2.common.events.ChangeLeaderEvent;
import com.aionemu.gameserver.network.aion.serverpackets.S_ALLIANCE_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.common.base.Predicate;

import java.util.Collection;

public class ChangeAllianceLeaderEvent extends ChangeLeaderEvent<PlayerAlliance>
{
    public ChangeAllianceLeaderEvent(PlayerAlliance team, Player eventPlayer) {
        super(team, eventPlayer);
    }
	
    public ChangeAllianceLeaderEvent(PlayerAlliance team) {
        super(team, null);
    }
	
    @Override
    public void handleEvent() {
        Player oldLeader = team.getLeaderObject();
        if (eventPlayer == null) {
            Collection<Integer> viceCaptainIds = team.getViceCaptainIds();
            for (Integer viceCaptainId : viceCaptainIds) {
                PlayerAllianceMember viceCaptain = team.getMember(viceCaptainId);
                if (viceCaptain.isOnline()) {
                    changeLeaderTo(viceCaptain.getObject());
                    viceCaptainIds.remove(viceCaptainId);
                    break;
                }
            } if (team.isLeader(oldLeader)) {
                team.applyOnMembers(this);
            }
        } else {
            changeLeaderTo(eventPlayer);
        }
        checkLeaderChanged(oldLeader);
        if (eventPlayer != null) {
            PlayerAllianceService.changeViceCaptain(oldLeader, AssignType.DEMOTE_CAPTAIN_TO_VICECAPTAIN);
        }
    }
	
    @Override
    protected void changeLeaderTo(final Player player) {
        team.changeLeader(team.getMember(player.getObjectId()));
        team.applyOnMembers(new Predicate<Player>() {
            @Override
            public boolean apply(Player member) {
                PacketSendUtility.sendPacket(member, new S_ALLIANCE_INFO(team));
                if (!player.equals(member)) {
                    PacketSendUtility.sendPacket(member, S_MESSAGE_CODE.STR_FORCE_HE_IS_NEW_LEADER(player.getName()));
                } else {
                    PacketSendUtility.sendPacket(member, S_MESSAGE_CODE.STR_FORCE_YOU_BECOME_NEW_LEADER);
                }
                return true;
            }
        });
    }
}