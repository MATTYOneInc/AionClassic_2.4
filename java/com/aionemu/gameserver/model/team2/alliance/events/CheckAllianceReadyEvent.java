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
import com.aionemu.gameserver.model.team2.common.events.AlwaysTrueTeamEvent;
import com.aionemu.gameserver.model.team2.common.events.TeamCommand;
import com.aionemu.gameserver.network.aion.serverpackets.S_GROUP_READY;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.common.base.Predicate;

/**
 * @author ATracer
 */
public class CheckAllianceReadyEvent extends AlwaysTrueTeamEvent implements Predicate<Player> {

    private final PlayerAlliance alliance;
    private final Player player;
    private final TeamCommand eventCode;

    public CheckAllianceReadyEvent(PlayerAlliance alliance, Player player, TeamCommand eventCode) {
        this.alliance = alliance;
        this.player = player;
        this.eventCode = eventCode;
    }

    @Override
    public void handleEvent() {
        int readyStatus = alliance.getAllianceReadyStatus();
        switch (eventCode) {
            case ALLIANCE_CHECKREADY_CANCEL:
                readyStatus = 0;
                break;
            case ALLIANCE_CHECKREADY_START:
                readyStatus = alliance.onlineMembers() - 1;
                break;
            case ALLIANCE_CHECKREADY_AUTOCANCEL:
                readyStatus = 0;
                break;
            case ALLIANCE_CHECKREADY_READY:
            case ALLIANCE_CHECKREADY_NOTREADY:
                readyStatus -= 1;
                break;
        }
        alliance.setAllianceReadyStatus(readyStatus);
        alliance.applyOnMembers(this);
    }

    @Override
    public boolean apply(Player member) {
        switch (eventCode) {
            case ALLIANCE_CHECKREADY_CANCEL:
                PacketSendUtility.sendPacket(member, new S_GROUP_READY(player.getObjectId(), 0));
                break;
            case ALLIANCE_CHECKREADY_START:
                PacketSendUtility.sendPacket(member, new S_GROUP_READY(player.getObjectId(), 5));
                PacketSendUtility.sendPacket(member, new S_GROUP_READY(player.getObjectId(), 1));
                break;
            case ALLIANCE_CHECKREADY_AUTOCANCEL:
                PacketSendUtility.sendPacket(member, new S_GROUP_READY(player.getObjectId(), 2));
                break;
            case ALLIANCE_CHECKREADY_READY:
                PacketSendUtility.sendPacket(member, new S_GROUP_READY(player.getObjectId(), 5));
                if (alliance.getAllianceReadyStatus() == 0) {
                    PacketSendUtility.sendPacket(member, new S_GROUP_READY(0, 3));
                }
                break;
            case ALLIANCE_CHECKREADY_NOTREADY:
                PacketSendUtility.sendPacket(member, new S_GROUP_READY(player.getObjectId(), 4));
                if (alliance.getAllianceReadyStatus() == 0) {
                    PacketSendUtility.sendPacket(member, new S_GROUP_READY(0, 3));
                }
                break;
        }
        return true;
    }
}
