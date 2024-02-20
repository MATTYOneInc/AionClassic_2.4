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

public class SiegeConfig
{
	@Property(key = "gameserver.siege.enable", defaultValue = "true")
	public static boolean SIEGE_ENABLED;
	
	@Property(key = "gameserver.siege.medal.rate", defaultValue = "1")
	public static int SIEGE_MEDAL_RATE;
	
	@Property(key = "gameserver.siege.shield.enable", defaultValue = "true")
	public static boolean SIEGE_SHIELD_ENABLED;
	
	@Property(key = "gameserver.siege.assault.rate", defaultValue = "80")
	public static float BALAUR_ASSAULT_RATE;
	
	@Property(key = "gameserver.auto.siege.race", defaultValue = "false")
	public static boolean SIEGE_AUTO_RACE;
	
	@Property(key = "gameserver.auto.siege.id", defaultValue = "1011,1131;1132,1141;1211,1221;1231,1241;1251,2011;2021,3011;3021")
	public static String SIEGE_AUTO_LOCID;
}