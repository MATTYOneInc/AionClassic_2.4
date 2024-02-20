package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class HTMLConfig
{
	@Property(key = "gameserver.html.welcome.enable", defaultValue = "false")
	public static boolean ENABLE_HTML_WELCOME;
	@Property(key = "gameserver.html.guides.enable", defaultValue = "false")
	public static boolean ENABLE_GUIDES;
	@Property(key = "gameserver.html.root", defaultValue = "./data/static_data/HTML/")
	public static String HTML_ROOT;
	@Property(key = "gameserver.html.cache.file", defaultValue = "./cache/html.cache")
	public static String HTML_CACHE_FILE;
	@Property(key = "gameserver.html.encoding", defaultValue = "UTF-8")
	public static String HTML_ENCODING;
}