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
package com.aionemu.gameserver.utils.gametime;

/**
 * Responsible for updating the clock
 * 
 * @author Ben
 */
public class GameTimeUpdater implements Runnable {

	private GameTime time;

	/**
	 * Constructs GameTimeUpdater to update the given GameTime
	 * 
	 * @param time
	 *          GameTime to update
	 */
	public GameTimeUpdater(GameTime time) {
		this.time = time;
	}

	/**
	 * Increases the time by one minute
	 */
	@Override
	public void run() {
		time.increase();
	}
}
