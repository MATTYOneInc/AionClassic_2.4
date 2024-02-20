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
package com.aionemu.gameserver.model;

/**
 * @author zdead
 */
public enum PetitionType {
	CHARACTER_STUCK(256),
	CHARACTER_RESTORATION(512),
	BUG(768),
	QUEST(1024),
	UNACCEPTABLE_BEHAVIOR(1280),
	SUGGESTION(1536),
	INQUIRY(65280);

	private int element;

	private PetitionType(int id) {
		this.element = id;
	}

	public int getElementId() {
		return element;
	}
}
