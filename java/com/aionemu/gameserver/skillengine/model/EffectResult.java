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

public enum EffectResult
{
	NORMAL(0),
    ABSORBED(1),
    CONFLICT(2),
    DODGE(3),
    RESIST(4);
	
	private int id;
	
	private EffectResult(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}