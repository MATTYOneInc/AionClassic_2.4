package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

import java.util.regex.Pattern;

public class NameConfig
{
	@Property(key = "gameserver.name.characterpattern", defaultValue = "[a-zA-Z]{2,16}")
	public static Pattern CHAR_NAME_PATTERN;
	@Property(key = "gameserver.name.forbidden.sequences", defaultValue = "")
	public static String NAME_SEQUENCE_FORBIDDEN;
	@Property(key = "gameserver.name.forbidden.enable.client", defaultValue = "true")
	public static boolean NAME_FORBIDDEN_ENABLE;
	@Property(key = "gameserver.name.forbidden.client", defaultValue = "")
	public static String NAME_FORBIDDEN_CLIENT;
	@Property(key = "gameserver.pet.name.change.enable", defaultValue = "true")
	public static boolean PET_NAME_CHANGE_ENABLE;
}