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
package com.aionemu.gameserver.skillengine.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * @author Cheatkiller
 *
 */
@XmlType(name = "StigmaType")
@XmlEnum
public enum StigmaType {
	
	NONE(0),
	BASIC(1),
	ADVANCED(2);
	
	private int id;

	private StigmaType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
