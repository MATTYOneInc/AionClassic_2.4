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
package com.aionemu.gameserver.model.templates.pet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Rinzler
 */

@XmlType(name = "merch")
@XmlAccessorType(XmlAccessType.NONE)
public class PetMerchandEntry
{
	@XmlAttribute(name = "id", required = true)
	private int id;
	
	@XmlAttribute(name = "rate_price")
	private int ratePrice;
	
	public int getId() {
		return id;
	}
	
	public int getRatePrice() {
		return ratePrice;
	}
}