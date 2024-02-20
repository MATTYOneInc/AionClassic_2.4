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

import com.aionemu.gameserver.model.team2.TeamEvent;

/**
 * @author ATracer
 */
public abstract class AlwaysTrueTeamEvent implements TeamEvent {

    @Override
    public final boolean checkCondition() {
        return true;
    }
}
