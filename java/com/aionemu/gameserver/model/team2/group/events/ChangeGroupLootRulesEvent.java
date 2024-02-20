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
import com.aionemu.gameserver.model.team2.common.legacy.LootGroupRules;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.network.aion.serverpackets.S_PARTY_INFO;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.common.base.Predicate;

/**
 * @author ATracer
 */
public class ChangeGroupLootRulesEvent extends AlwaysTrueTeamEvent implements Predicate<Player> {

    private final PlayerGroup group;
    private final LootGroupRules lootGroupRules;

    public ChangeGroupLootRulesEvent(PlayerGroup group, LootGroupRules lootGroupRules) {
        this.group = group;
        this.lootGroupRules = lootGroupRules;
    }

    @Override
    public boolean apply(Player member) {
        PacketSendUtility.sendPacket(member, new S_PARTY_INFO(group));
        return true;
    }

    @Override
    public void handleEvent() {
        group.setLootGroupRules(lootGroupRules);
        group.applyOnMembers(this);
    }
}
