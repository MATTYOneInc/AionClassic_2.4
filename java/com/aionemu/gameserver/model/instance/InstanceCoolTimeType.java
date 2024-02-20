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
package com.aionemu.gameserver.model.instance;

public enum InstanceCoolTimeType
{
    RELATIVE,
    WEEKLY,
    DAILY;
	
    public boolean isRelative() {
        return this.equals(InstanceCoolTimeType.RELATIVE);
    }
	
    public boolean isWeekly() {
        return this.equals(InstanceCoolTimeType.WEEKLY);
    }
	
    public boolean isDaily() {
        return this.equals(InstanceCoolTimeType.DAILY);
    }
}