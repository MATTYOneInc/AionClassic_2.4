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
 * @author Lyahim
 */
public enum LootRuleType {

    FREEFORALL(0),
    ROUNDROBIN(1),
    LEADER(2);
    private int id;

    private LootRuleType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
