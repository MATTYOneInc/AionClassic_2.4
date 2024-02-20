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
package com.aionemu.gameserver.utils.stats.enums;

public enum HEALTH
{
	WARRIOR(110),
	GLADIATOR(110),
	TEMPLAR(110),
	SCOUT(100),
	ASSASSIN(100),
	RANGER(100),
	MAGE(90),
	SORCERER(90),
	SPIRIT_MASTER(90),
	PRIEST(95),
	CLERIC(95),
	CHANTER(95),
	MONK(100),
	THUNDERER(100);
	
	private int value;
	
	private HEALTH(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}