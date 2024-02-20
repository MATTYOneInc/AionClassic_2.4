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

public enum AGILITY
{
	WARRIOR(100),
	GLADIATOR(100),
	TEMPLAR(100),
	SCOUT(110),
	ASSASSIN(110),
	RANGER(110),
	MAGE(95),
	SORCERER(95),
	SPIRIT_MASTER(95),
	PRIEST(100),
	CLERIC(100),
	CHANTER(100),
	MONK(110),
	THUNDERER(110);
	
	private int value;
	
	private AGILITY(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}