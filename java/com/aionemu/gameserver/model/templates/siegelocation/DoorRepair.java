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
package com.aionemu.gameserver.model.templates.siegelocation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @Author Rinzler (Encom)
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DoorRepair")
public class DoorRepair
{
	@XmlAttribute(name = "itemid")
	protected int itemId;
	
	@XmlAttribute(name = "repair_fee")
	protected int repairFee;
	
	@XmlAttribute(name = "repair_cooltime")
	protected int repairCooltime;
	
	public int getItemId() {
		return itemId;
	}
	
	public int getRepairFee() {
		return repairFee;
	}
	
	public long getRepairCooltime() {
		return (long) (repairCooltime * 1000);
	}
}