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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/****/
/** Author Rinzler (Encom)
/****/

@XmlType(name = "MultiReturn")
public class MultiReturn
{
	@XmlAttribute(name = "id")
	private int id;
	
	@XmlElement(name = "loc")
	private List<MultiReturnLocationList> MultiReturnList;
	
	public int getId() {
		return id;
	}
	
	public MultiReturnLocationList getReturnDataById(int id) {
		if (MultiReturnList != null) {
			return MultiReturnList.get(id);
		}
		return null;
	}
	
	public List<MultiReturnLocationList> getMultiReturnList() {
		return MultiReturnList;
	}
}