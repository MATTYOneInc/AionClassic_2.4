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
package com.aionemu.gameserver.model.team2;

public enum TeamType
{
    GROUP(0x3F, 0),
    AUTO_GROUP(0x02, 1),
    ALLIANCE(0x3F, 0),
    ALLIANCE_DEFENCE(0x3F, 4),
    ALLIANCE_OFFENCE(0x02, 3);
	
    private int type;
    private int subType;
	
    private TeamType(int type, int subType) {
        this.type = type;
        this.subType = subType;
    }
	
    public int getType() {
        return type;
    }
	
    public int getSubType() {
        return subType;
    }
	
    public boolean isAutoTeam() {
        return this.getType() == 0x02;
    }
	
    public boolean isOffence() {
        return this.getSubType() == 3;
    }
	
    public boolean isDefence() {
        return this.getSubType() == 4;
    }
}