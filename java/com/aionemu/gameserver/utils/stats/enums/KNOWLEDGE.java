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

public enum KNOWLEDGE
{
	WARRIOR(90),
	GLADIATOR(90),
	TEMPLAR(90),
	SCOUT(90),
	ASSASSIN(90),
	RANGER(90),
	MAGE(115),
	SORCERER(115),
	SPIRIT_MASTER(115),
	PRIEST(100),
	CLERIC(100),
	CHANTER(100),
	MONK(90),
	THUNDERER(90);
	
	private int value;
	
	private KNOWLEDGE(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}