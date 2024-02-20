package com.aionemu.gameserver.configs.administration;

import com.aionemu.commons.configuration.Property;

public class DeveloperConfig
{
	@Property(key = "gameserver.developer.spawn.enable", defaultValue = "true")
	public static boolean SPAWN_ENABLE;
	@Property(key = "gameserver.developer.itemstat.id", defaultValue = "0")
	public static int ITEM_STAT_ID;
	@Property(key = "gameserver.developer.spawn.check", defaultValue = "false")
	public static boolean SPAWN_CHECK;
}