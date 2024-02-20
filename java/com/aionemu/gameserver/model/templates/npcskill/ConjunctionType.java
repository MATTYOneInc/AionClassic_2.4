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
package com.aionemu.gameserver.model.templates.npcskill;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * @author nrg
 */

@XmlType(name = "ConjunctionType")
@XmlEnum
public enum ConjunctionType {

	AND,
	OR,
	XOR;

	public String value() {
		return name();
	}

	public static ConjunctionType fromValue(String v) {
		return valueOf(v);
	}

}
