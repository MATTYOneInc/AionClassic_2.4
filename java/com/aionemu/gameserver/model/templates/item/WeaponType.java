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
package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Rinzler (Encom)
 */

@XmlType(name = "weapon_type")
@XmlEnum
public enum WeaponType
{
	SWORD_1H(new int[] {1, 8, 336, 342, 368}, 1),
	DAGGER_1H(new int[] {2, 9, 30, 337, 343, 369}, 1),
	MACE_1H(new int[] {3, 10, 338, 344, 370}, 1),
	BOOK_2H(new int[] {64, 1551, 1552, 1580}, 2),
	ORB_2H(new int[] {64, 1743, 1745, 1777}, 2),
	SWORD_2H(new int[] {15, 339, 345, 371}, 2),
	POLEARM_2H(new int[] {16, 340, 346, 372}, 2),
	STAFF_2H(new int[] {53, 1157, 1159, 1175}, 2),
	BOW(new int[] {17, 341, 347, 373}, 2),
	BLADE_2H(new int[] {2520}, 2),
	TOOLHOE_1H(new int[] {}, 1),
	TOOLPICK_2H(new int[] {}, 2),
	TOOLROD_2H(new int[] {}, 2);
	
	private int slots;
	private int[] requiredSkill;
	
	private WeaponType(int[] requiredSkills, int slots) {
		this.requiredSkill = requiredSkills;
		this.slots = slots;
	}
	
	public int[] getRequiredSkills() {
		return requiredSkill;
	}
	
	public int getRequiredSlots() {
		return slots;
	}
	
	public int getMask() {
		return 1 << this.ordinal();
	}
}