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

package com.aionemu.gameserver.model;

import javax.xml.bind.annotation.XmlEnum;

/**
 * Creature gender. Typically there are males and females. But who knows, maybe NC can invent something new ;)
 * 
 * @author SoulKeeper
 */
@XmlEnum
public enum Gender {
	/**
	 * Males
	 */
	MALE(0),

	/**
	 * Females
	 */
	FEMALE(1);

	/**
	 * id of gender
	 */
	private int genderId;

	/**
	 * Constructor.
	 * 
	 * @param genderId
	 *          id of the gender
	 */
	private Gender(int genderId) {
		this.genderId = genderId;
	}

	/**
	 * Get id of this gender.
	 * 
	 * @return gender id
	 */
	public int getGenderId() {
		return genderId;
	}
}
