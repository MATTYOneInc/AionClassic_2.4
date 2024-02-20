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
import com.aionemu.gameserver.network.aion.serverpackets.S_TACTICS_SIGN;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.google.common.base.Predicate;

/**
 * @author ATracer
 */
public class ShowBrandEvent<T extends TemporaryPlayerTeam<? extends TeamMember<Player>>> extends AlwaysTrueTeamEvent
        implements Predicate<Player> {

    private final T team;
    private final int targetObjId;
    private final int brandId;

    public ShowBrandEvent(T team, int targetObjId, int brandId) {
        this.team = team;
        this.targetObjId = targetObjId;
        this.brandId = brandId;
    }

    @Override
    public void handleEvent() {
        team.applyOnMembers(this);
    }

    @Override
    public boolean apply(Player member) {
        PacketSendUtility.sendPacket(member, new S_TACTICS_SIGN(brandId, targetObjId));
        return true;
    }
}
