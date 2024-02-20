package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class LegionConfig
{
	protected static final Logger log = LoggerFactory.getLogger(LegionConfig.class);
	@Property(key = "gameserver.legion.pattern", defaultValue = "[a-zA-Z ]{2,32}")
	public static Pattern LEGION_NAME_PATTERN;
	@Property(key = "gameserver.legion.self.intro.pattern", defaultValue = ".{1,32}")
	public static Pattern SELF_INTRO_PATTERN;
	@Property(key = "gameserver.legion.nick.name.pattern", defaultValue = ".{1,10}")
	public static Pattern NICKNAME_PATTERN;
	@Property(key = "gameserver.legion.announcement.pattern", defaultValue = ".{1,256}")
	public static Pattern ANNOUNCEMENT_PATTERN;
	
	@Property(key = "gameserver.legion.disband.time", defaultValue = "86400")
	public static int LEGION_DISBAND_TIME;
	@Property(key = "gameserver.legion.disband.difference", defaultValue = "604800")
	public static int LEGION_DISBAND_DIFFERENCE;

	@Property(key = "gameserver.legion.emblem.required.kinah", defaultValue = "10000")
	public static int LEGION_EMBLEM_REQUIRED_KINAH;

	@Property(key = "gameserver.legion.warehouse", defaultValue = "true")
	public static boolean LEGION_WAREHOUSE;
	@Property(key = "gameserver.legion.invite.other.faction", defaultValue = "false")
	public static boolean LEGION_INVITEOTHERFACTION;

	@Property(key = "gameserver.guild.growth.level.max", defaultValue = "100")
	public static int GUILD_GROWTH_LEVEL_MAX;
	@Property(key = "gameserver.guild.growth.exp.rate", defaultValue = "1")
	public static int GUILD_GROWTH_EXP_RATE;
}