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
package com.aionemu.gameserver.model.autogroup;

public enum EntryRequestType
{
	NEW_GROUP_ENTRY((byte) 0),
	FAST_GROUP_ENTRY((byte) 1),
	GROUP_ENTRY((byte) 2);
	
	private byte id;
	
	private EntryRequestType(byte id) {
		this.id = id;
	}
	
	public byte getId() {
		return id;
	}
	
	public boolean isNewGroupEntry() {
		return id == 0;
	}
	
	public boolean isFastGroupEntry() {
		return id == 1;
	}
	
	public boolean isGroupEntry() {
		return id == 2;
	}
	
	public static EntryRequestType getTypeById(byte id) {
		for (EntryRequestType ert : values()) {
			if (ert.getId() == id) {
				return ert;
			}
		}
		return null;
	}
}