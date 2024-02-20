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
package com.aionemu.gameserver.model.templates.teleport;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/****/
/** Author Rinzler (Encom)
/****/

@XmlType(name = "MultiReturnLocationList")
public class MultiReturnLocationList
{
	@XmlAttribute(name = "world_id")
	protected int worldId;
	
	@XmlAttribute(name = "desc")
	protected String desc;
	
	public final int getWorldId() {
		return worldId;
	}
	
	public final String getDesc() {
		return desc;
	}
}