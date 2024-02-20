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
package com.aionemu.gameserver.model.siege;


/**
 * @author MrPoke
 *
 */
public enum ArtifactStatus {
	IDLE(0),
	ACTIVATION(1),
	CASTING(2),
	ACTIVATED(3);
	
	private int id;

	ArtifactStatus(int id){
		this.id = id;
	}
	
	public int getValue(){
		return id;
	}
}
