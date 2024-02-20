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

public enum FLY_SPEED
{
	WARRIOR(9),
	GLADIATOR(9),
	TEMPLAR(9),
	SCOUT(9),
	ASSASSIN(9),
	RANGER(9),
	MAGE(9),
	SORCERER(9),
	SPIRIT_MASTER(9),
	PRIEST(9),
	CLERIC(9),
	CHANTER(9),
	MONK(9),
	THUNDERER(9);
	
	private int value;
	
	private FLY_SPEED(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}