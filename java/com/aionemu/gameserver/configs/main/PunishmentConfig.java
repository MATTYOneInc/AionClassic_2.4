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
 * @author synchro2
 */
public class PunishmentConfig {

	@Property(key = "gameserver.punishment.enable", defaultValue = "false")
	public static boolean PUNISHMENT_ENABLE;

	@Property(key = "gameserver.punishment.type", defaultValue = "1")
	public static int PUNISHMENT_TYPE;

	@Property(key = "gameserver.punishment.time", defaultValue = "1440")
	public static int PUNISHMENT_TIME;
  @Property(key="gameserver.punishment.reduceap", defaultValue="0")
  public static int PUNISHMENT_REDUCEAP;
}