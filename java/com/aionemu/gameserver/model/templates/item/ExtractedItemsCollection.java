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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author antness
 */
@XmlType(name = "ExtractedItemsCollection")
public class ExtractedItemsCollection extends ResultedItemsCollection {

	@XmlAttribute(name = "chance")
	protected float chance = 100;
	@XmlAttribute(name = "minlevel")
	protected int minLevel;
	@XmlAttribute(name = "maxlevel")
	protected int maxLevel;

	public final float getChance() {
		return chance;
	}

	public final int getMinLevel() {
		return minLevel;
	}

	public final int getMaxLevel() {
		return maxLevel;
	}

}
