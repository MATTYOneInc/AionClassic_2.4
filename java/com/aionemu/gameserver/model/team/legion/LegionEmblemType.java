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
package com.aionemu.gameserver.model.team.legion;

/**
 * @author cura
 */
public enum LegionEmblemType {
	DEFAULT(0x00),
	CUSTOM(0x80);

	private byte value;

	private LegionEmblemType(int value) {
		this.value = (byte) value;
	}

	public byte getValue() {
		return value;
	}
}
