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

public enum GroupEvent
{
    LEAVE(0),
    MOVEMENT(1),
    DISCONNECTED(3),
    JOIN(5),
    ENTER_OFFLINE(7),
    ENTER(13),
    UPDATE(13),
    UNK(9);
	
    private int id;
	
    private GroupEvent(int id) {
        this.id = id;
    }
	
    public int getId() {
        return this.id;
    }
}