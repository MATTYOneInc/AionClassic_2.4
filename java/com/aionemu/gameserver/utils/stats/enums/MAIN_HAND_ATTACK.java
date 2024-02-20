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

public enum MAIN_HAND_ATTACK
{
	WARRIOR(19),
	GLADIATOR(19),
	TEMPLAR(19),
	SCOUT(18),
	ASSASSIN(19),
	RANGER(18),
	MAGE(14),
	SORCERER(16),
	SPIRIT_MASTER(16),
	PRIEST(18),
	CLERIC(18),
	CHANTER(19),
	MONK(19),
	THUNDERER(19);
	
	private int value;
	
	private MAIN_HAND_ATTACK(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}