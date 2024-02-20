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

public enum MAIN_HAND_CRITRATE
{
	WARRIOR(2),
	GLADIATOR(2),
	TEMPLAR(2),
	SCOUT(3),
	ASSASSIN(3),
	RANGER(3),
	MAGE(2),
	SORCERER(2),
	SPIRIT_MASTER(2),
	PRIEST(2),
	CLERIC(2),
	CHANTER(1),
	MONK(2),
	THUNDERER(2);
	
	private int value;
	
	private MAIN_HAND_CRITRATE(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}