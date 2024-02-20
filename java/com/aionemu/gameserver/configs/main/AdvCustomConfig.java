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

public class AdvCustomConfig
{
    @Property(key = "gameserver.cube.size", defaultValue = "0")
    public static int CUBE_SIZE;
	
	@Property(key = "gameserver.gameshop.limit", defaultValue = "false")
	public static boolean GAMESHOP_LIMIT;
	
	@Property(key = "gameserver.gameshop.category", defaultValue = "0")
	public static byte GAMESHOP_CATEGORY;
	
	@Property(key = "gameserver.gameshop.limit.time", defaultValue = "60")
	public static long GAMESHOP_LIMIT_TIME;
	
	@Property(key = "gameserver.craft.delaytime,rate", defaultValue = "2")
	public static Integer CRAFT_DELAYTIME_RATE;
}