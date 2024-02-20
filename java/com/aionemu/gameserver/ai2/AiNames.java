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
package com.aionemu.gameserver.ai2;

/**
 * @author ATracer
 */
public enum AiNames {

	GENERAL_NPC("general"),
	DUMMY_NPC("dummy"),
	AGGRESSIVE_NPC("aggressive");

	private final String name;

	private AiNames(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
