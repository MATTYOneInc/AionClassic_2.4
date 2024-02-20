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

public class AutoGroupConfig
{
	@Property(key = "gameserver.autogroup.enable", defaultValue = "true")
	public static boolean AUTO_GROUP_ENABLED;
	
	@Property(key = "gameserver.dredgion.timer", defaultValue = "60")
	public static long DREDGION_TIMER;
	@Property(key = "gameserver.dredgion.enable", defaultValue = "true")
	public static boolean DREDGION_ENABLED;
	@Property(key = "gameserver.dredgion.schedule.midday", defaultValue = "0 0 12 ? * MON-SUN *")
	public static String DREDGION_SCHEDULE_MIDDAY;
	@Property(key = "gameserver.dredgion.schedule.evening", defaultValue = "0 0 18 ? * MON-SUN *")
	public static String DREDGION_SCHEDULE_EVENING;
	@Property(key = "gameserver.dredgion.schedule.midnight", defaultValue = "0 0 0 ? * MON-SUN *")
	public static String DREDGION_SCHEDULE_MIDNIGHT;
	
	@Property(key = "gameserver.tiak.base.timer", defaultValue = "120")
	public static long TIAK_BASE_TIMER;
	@Property(key = "gameserver.tiak.base.enable", defaultValue = "true")
	public static boolean TIAK_BASE_ENABLED;
	@Property(key = "gameserver.tiak.base.schedule.evening", defaultValue = "0 0 20 ? * MON-SUN *")
	public static String TIAK_BASE_SCHEDULE_EVENING;
}