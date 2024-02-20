package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class PeriodicSaveConfig
{
	@Property(key = "gameserver.periodicsave.player.general", defaultValue = "900")
	public static int PLAYER_GENERAL;
	@Property(key = "gameserver.periodicsave.player.items", defaultValue = "900")
	public static int PLAYER_ITEMS;
	@Property(key = "gameserver.periodicsave.legion.items", defaultValue = "1200")
	public static int LEGION_ITEMS;
	@Property(key = "gameserver.periodicsave.broker", defaultValue = "1500")
	public static int BROKER;
	@Property(key = "gameserver.periodicsave.player.pets", defaultValue = "5")
	public static int PLAYER_PETS;
	@Property(key = "gameserver.periodicsave.player.minions", defaultValue = "5")
	public static int PLAYER_MINIONS;
}