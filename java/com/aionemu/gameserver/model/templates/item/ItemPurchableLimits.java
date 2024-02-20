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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Ranastic (Encom)
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Purchable")
public class ItemPurchableLimits
{
	@XmlAttribute(name = "rank_min")
	private int minRank;
	
	public int getMinRank() {
		return minRank;
	}
	
	public boolean verifyRank(int rank) {
		return minRank <= rank;
	}
}