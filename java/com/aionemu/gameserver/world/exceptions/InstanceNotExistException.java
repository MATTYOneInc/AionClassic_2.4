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
package com.aionemu.gameserver.world.exceptions;

/**
 * This Exception will be thrown when some object is referencing to Instance that do not exist now.
 * 
 * @author -Nemesiss-
 */
@SuppressWarnings("serial")
public class InstanceNotExistException extends RuntimeException {

	/**
	 * Constructs an <code>InstanceNotExistException</code> with no detail message.
	 */
	public InstanceNotExistException() {
		super();
	}

	/**
	 * Constructs an <code>InstanceNotExistException</code> with the specified detail message.
	 * 
	 * @param s
	 *          the detail message.
	 */
	public InstanceNotExistException(String s) {
		super(s);
	}
}
