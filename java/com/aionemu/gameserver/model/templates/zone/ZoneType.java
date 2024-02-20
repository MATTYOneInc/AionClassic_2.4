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
package com.aionemu.gameserver.model.templates.zone;

public enum ZoneType
{
	FLY(0),
	DAMAGE(1),
	WATER(2),
	SIEGE(3),
	PVP(4),
	RIDE(5);
	
	private byte value;
	
	private ZoneType(int value) {
		this.value = (byte)value;
	}
	
	public byte getValue() {
		return value;
	}
}