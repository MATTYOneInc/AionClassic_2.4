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

public enum AGQuestion
{
	FAILED,
	READY,
	ADDED;
	
	public boolean isFailed() {
		return this.equals(AGQuestion.FAILED);
	}
	
	public boolean isReady() {
		return this.equals(AGQuestion.READY);
	}
	
	public boolean isAdded() {
		return this.equals(AGQuestion.ADDED);
	}
}