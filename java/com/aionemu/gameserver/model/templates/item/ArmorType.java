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

@XmlType(name = "armor_type")
@XmlEnum
public enum ArmorType
{
	CHAIN(new int[] {6, 13}),
	CLOTHES(new int[] {4}),
	LEATHER(new int[] {5, 12}),
	PLATE(new int[] {18}),
	ROBE(new int[] {67, 70}),
	SHIELD(new int[] {7, 14}),
	NO_ARMOR(new int[] {}),
	SHARD(new int[] {}),
	WING(new int[] {}),
	ARROW(new int[] {});
	
	private int[] requiredSkills;
	
	private ArmorType(int[] requiredSkills) {
		this.requiredSkills = requiredSkills;
	}
	
	public int[] getRequiredSkills() {
		return requiredSkills;
	}
	
	public int getMask() {
		return 1 << this.ordinal();
	}
}