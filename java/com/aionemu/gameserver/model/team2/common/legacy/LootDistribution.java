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
package com.aionemu.gameserver.model.team2.common.legacy;

/**
 * @author KKnD
 */
public enum LootDistribution {

    NORMAL(0),
    ROLL_DICE(2),
    BID(3);
    private int id;

    LootDistribution(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
