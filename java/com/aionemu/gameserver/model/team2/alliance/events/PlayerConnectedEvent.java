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
import com.aionemu.gameserver.model.team2.common.events.AlwaysTrueTeamEvent;
import com.aionemu.gameserver.model.team2.common.legacy.PlayerAllianceEvent;
import com.aionemu.gameserver.network.aion.serverpackets.S_ALLIANCE_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_ALLIANCE_MEMBER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_INSTANCE_DUNGEON_COOLTIMES;
import com.aionemu.gameserver.network.aion.serverpackets.S_TACTICS_SIGN;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.common.base.Predicate;

/**
 * @author ATracer
 */
public class PlayerConnectedEvent extends AlwaysTrueTeamEvent implements Predicate<PlayerAllianceMember> {

    private final PlayerAlliance alliance;
    private final Player connected;
    private PlayerAllianceMember connectedMember;

    public PlayerConnectedEvent(PlayerAlliance alliance, Player player) {
        this.alliance = alliance;
        this.connected = player;
    }

    @Override
    public void handleEvent() {
        alliance.removeMember(connected.getObjectId());
        connectedMember = new PlayerAllianceMember(connected);
        alliance.addMember(connectedMember);

        PacketSendUtility.sendPacket(connected, new S_ALLIANCE_INFO(alliance));
        PacketSendUtility
                .sendPacket(connected, new S_ALLIANCE_MEMBER_INFO(connectedMember, PlayerAllianceEvent.RECONNECT));
        PacketSendUtility.sendPacket(connected, new S_TACTICS_SIGN(0, 0));

        alliance.apply(this);
    }

    @Override
    public boolean apply(PlayerAllianceMember member) {
        Player player = member.getObject();
        if (!connected.getObjectId().equals(player.getObjectId())) {
            PacketSendUtility.sendPacket(player, new S_ALLIANCE_MEMBER_INFO(connectedMember, PlayerAllianceEvent.RECONNECT));
            PacketSendUtility.sendPacket(player, new S_INSTANCE_DUNGEON_COOLTIMES(connected, false, alliance));

            PacketSendUtility.sendPacket(connected, new S_ALLIANCE_MEMBER_INFO(member, PlayerAllianceEvent.RECONNECT));
        }
        return true;
    }
}
