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
package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

/**
 * @author ATracer
 */
public class AIConfig {

	/**
	 * Debug (for developers)
	 */
	@Property(key = "gameserver.ai.move.debug", defaultValue = "true")
	public static boolean MOVE_DEBUG;

	@Property(key = "gameserver.ai.event.debug", defaultValue = "false")
	public static boolean EVENT_DEBUG;

	@Property(key = "gameserver.ai.oncreate.debug", defaultValue = "false")
	public static boolean ONCREATE_DEBUG;

	/**
	 * Enable NPC movement
	 */
	@Property(key = "gameserver.npcmovement.enable", defaultValue = "true")
	public static boolean ACTIVE_NPC_MOVEMENT;

	/**
	 * Minimum movement delay
	 */
	@Property(key = "gameserver.npcmovement.delay.minimum", defaultValue = "3")
	public static int MINIMIMUM_DELAY;

	/**
	 * Maximum movement delay
	 */
	@Property(key = "gameserver.npcmovement.delay.maximum", defaultValue = "15")
	public static int MAXIMUM_DELAY;

	/**
	 * Npc Shouts activator
	 */
	@Property(key = "gameserver.npcshouts.enable", defaultValue = "false")
	public static boolean SHOUTS_ENABLE;
}
