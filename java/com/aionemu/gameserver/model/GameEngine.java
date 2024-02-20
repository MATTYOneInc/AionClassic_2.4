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

import java.util.concurrent.CountDownLatch;

/**
 * @author ATracer
 */
public interface GameEngine {

	/**
	 * Load resources for engine
	 */
	void load(CountDownLatch progressLatch);

	/**
	 * Cleanup resources for engine
	 */
	void shutdown();
}
