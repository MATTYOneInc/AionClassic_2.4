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
 * @author Sarynth
 */
public enum PlayerAllianceEvent
{
    LEAVE(0),
    LEAVE_TIMEOUT(0),
    BANNED(0),
    MOVEMENT(1),
    DISCONNECTED(3),
    JOIN(5),
    ENTER_OFFLINE(7),
    UNK(9),
    RECONNECT(13),
    ENTER(13),
    UPDATE(13),
    MEMBER_GROUP_CHANGE(5),
    APPOINT_VICE_CAPTAIN(13),
    DEMOTE_VICE_CAPTAIN(13),
    APPOINT_CAPTAIN(13),
    UPDATE_BUFF(65);
	
    private int id;
	
    private PlayerAllianceEvent(int id) {
        this.id = id;
    }
	
    public int getId() {
        return this.id;
    }
}