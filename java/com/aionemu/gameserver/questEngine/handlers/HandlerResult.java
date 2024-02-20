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
package com.aionemu.gameserver.questEngine.handlers;


/**
 * @author Rolandas
 *
 */
public enum HandlerResult {
	UNKNOWN, // allow other handlers to process
	SUCCESS,
	FAILED;
	
	public static HandlerResult fromBoolean(Boolean value) {
		if (value == null)
			return HandlerResult.UNKNOWN;
		else if (value)
			return HandlerResult.SUCCESS;
		return HandlerResult.FAILED;
	}
}
