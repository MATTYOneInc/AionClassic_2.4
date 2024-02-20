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
import com.aionemu.gameserver.model.team2.common.events.AlwaysTrueTeamEvent;
import com.aionemu.gameserver.model.team2.common.legacy.GroupEvent;
import com.aionemu.gameserver.model.team2.group.PlayerFilters.MentorSuiteFilter;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.network.aion.serverpackets.S_ETC_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.S_PARTY_MEMBER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.google.common.base.Predicate;

/**
 * @author ATracer
 */
public class PlayerStartMentoringEvent extends AlwaysTrueTeamEvent implements Predicate<Player> {

    private final PlayerGroup group;
    private final Player player;

    public PlayerStartMentoringEvent(PlayerGroup group, Player player) {
        this.group = group;
        this.player = player;
    }

    @Override
    public void handleEvent() {
        if (group.filterMembers(new MentorSuiteFilter(player)).size() == 0) {
            AuditLogger.info(player, "Send fake start mentoring packet");
            return;
        }
        player.setMentor(true);
        PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_MENTOR_START);
        group.applyOnMembers(this);
        PacketSendUtility.broadcastPacketAndReceive(player, new S_ETC_STATUS(2, player));
    }

    @Override
    public boolean apply(Player member) {
        if (!player.equals(member)) {
            PacketSendUtility.sendPacket(member, S_MESSAGE_CODE.STR_MSG_MENTOR_START_PARTYMSG(player.getName()));
        }
        PacketSendUtility.sendPacket(member, new S_PARTY_MEMBER_INFO(group, player, GroupEvent.MOVEMENT));
        return true;
    }
}
