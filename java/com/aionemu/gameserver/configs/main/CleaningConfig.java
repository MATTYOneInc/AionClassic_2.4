package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class CleaningConfig
{

	@Property(key="gameserver.cleaning.enable", defaultValue="false")
	public static boolean CLEANING_ENABLE;

	@Property(key="gameserver.cleaning.period", defaultValue="180")
	public static int CLEANING_PERIOD;

	@Property(key="gameserver.cleaning.threads", defaultValue="2")
	public static int CLEANING_THREADS;

	@Property(key="gameserver.cleaning.limit", defaultValue="5000")
	public static int CLEANING_LIMIT;

	@Property(key = "gameserver.abyss.cleaning.enable", defaultValue = "false")
	public static boolean ABYSS_CLEANING_ENABLE;

	@Property(key = "gameserver.abyss.cleaning.period", defaultValue = "180")
	public static int ABYSS_CLEANING_PERIOD;
}