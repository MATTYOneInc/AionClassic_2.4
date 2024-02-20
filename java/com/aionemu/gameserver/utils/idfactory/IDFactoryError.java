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

package com.aionemu.gameserver.utils.idfactory;

/**
 * This error is thrown by id factory
 * 
 * @author SoulKeeper
 */
@SuppressWarnings("serial")
public class IDFactoryError extends Error {

	public IDFactoryError() {

	}

	public IDFactoryError(String message) {
		super(message);
	}

	public IDFactoryError(String message, Throwable cause) {
		super(message, cause);
	}

	public IDFactoryError(Throwable cause) {
		super(cause);
	}
}
