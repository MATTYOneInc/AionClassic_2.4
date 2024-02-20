package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class DropConfig
{
	@Property(key = "gameserver.unique.drop.announce.enable", defaultValue = "true")
	public static boolean ENABLE_UNIQUE_DROP_ANNOUNCE;
}