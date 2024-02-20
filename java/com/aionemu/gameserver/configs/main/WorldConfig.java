package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class WorldConfig
{
	@Property(key = "gameserver.world.region.size", defaultValue = "128")
	public static int WORLD_REGION_SIZE;
	@Property(key = "gameserver.world.region.active.trace", defaultValue = "true")
	public static boolean WORLD_ACTIVE_TRACE;
	@Property(key = "gameserver.world.emulate.a.station", defaultValue = "true")
    public static boolean WORLD_EMULATE_A_STATION;
	@Property(key = "gameserver.world.max.twincount.usual", defaultValue = "1")
	public static int WORLD_MAX_TWINS_USUAL;
	@Property(key = "gameserver.world.max.twincount.beginner", defaultValue = "-1")
	public static int WORLD_MAX_TWINS_BEGINNER;
}