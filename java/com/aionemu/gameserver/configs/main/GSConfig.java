package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

import java.util.Calendar;

public class GSConfig
{
	@Property(key = "gameserver.country.code", defaultValue = "1")
	public static int SERVER_COUNTRY_CODE;
	@Property(key = "gameserver.version.code", defaultValue = "1.2")
	public static float SERVER_VERSION_CODE;
	@Property(key = "gameserver.name", defaultValue = "Encom")
	public static String SERVER_NAME;
	@Property(key = "gameserver.players.max.level", defaultValue = "80")
	public static int PLAYER_MAX_LEVEL;
	@Property(key = "gameserver.timezone", defaultValue = "")
	public static String TIME_ZONE_ID = Calendar.getInstance().getTimeZone().getID();
	@Property(key = "gameserver.chatserver.enable", defaultValue = "false")
	public static boolean ENABLE_CHAT_SERVER;
	@Property(key = "gameserver.revisiondisplay.enable", defaultValue = "false")
	public static boolean SERVER_MOTD_DISPLAYREV;
	@Property(key = "gameserver.character.creation.mode", defaultValue = "0")
	public static int CHARACTER_CREATION_MODE;
	@Property(key = "gameserver.character.limit.count", defaultValue = "8")
	public static int CHARACTER_LIMIT_COUNT;
	@Property(key = "gameserver.character.faction.limitation.mode", defaultValue = "0")
	public static int CHARACTER_FACTION_LIMITATION_MODE;
	@Property(key = "gameserver.ratio.limitation.enable", defaultValue = "false")
	public static boolean ENABLE_RATIO_LIMITATION;
	@Property(key = "gameserver.ratio.min.value", defaultValue = "60")
	public static int RATIO_MIN_VALUE;
	@Property(key = "gameserver.ratio.min.required.level", defaultValue = "10")
	public static int RATIO_MIN_REQUIRED_LEVEL;
	@Property(key = "gameserver.ratio.min.characters_count", defaultValue = "50")
	public static int RATIO_MIN_CHARACTERS_COUNT;
	@Property(key = "gameserver.ratio.high_player_count.disabling", defaultValue = "500")
	public static int RATIO_HIGH_PLAYER_COUNT_DISABLING;
	@Property(key = "gameserver.abyssranking.small.cache", defaultValue = "false")
	public static boolean ABYSSRANKING_SMALL_CACHE;
	@Property(key = "gameserver.character.reentry.time", defaultValue = "20")
	public static int CHARACTER_REENTRY_TIME;
	@Property(key = "gameserver.master.server.enable", defaultValue = "false")
	public static boolean MASTER_SERVER;
	@Property(key = "gameserver.starting.level", defaultValue = "1")
	public static int STARTING_LEVEL;
	@Property(key = "gameserver.startClass.maxLevel", defaultValue = "10")
	public static int STARTCLASS_MAXLEVEL;
	@Property(key = "gameserver.startClass.monk.maxLevel", defaultValue = "15")
	public static int STARTCLASS_MONK_MAXLEVEL;

	/**
	 * Memory Optimization and Garbage Collector Enabled
	 */
	@Property(key = "gameserver.gc.enable", defaultValue = "true")
	public static boolean ENABLE_MEMORY_GC;

	/**
	 * Memory Optimization Every X Minutes
	 */
	@Property(key = "gameserver.gc.optimization.time", defaultValue = "5")
	public static int GC_OPTIMIZATION_TIME;
}
