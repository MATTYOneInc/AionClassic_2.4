package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class EventsConfig
{
	@Property(key = "gameserver.event.enable", defaultValue = "true")
	public static boolean EVENT_ENABLED;
	@Property(key = "gameserver.enable.decor", defaultValue = "0")
    public static int ENABLE_DECOR;
	@Property(key = "gameserver.events.give.juice", defaultValue = "160009017")
	public static int EVENT_GIVE_JUICE;
	@Property(key = "gameserver.events.give.cake", defaultValue = "160010073")
	public static int EVENT_GIVE_CAKE;
	@Property(key = "gameserver.event.service.enable", defaultValue = "true")
	public static boolean ENABLE_EVENT_SERVICE;
	
	//Upgrade Arcade.
	@Property(key = "gameserver.upgrade.arcade.chance", defaultValue = "60")
	public static int EVENT_ARCADE_CHANCE;
	
	//Event Window.
	@Property(key = "gameserver.event.window.enable", defaultValue = "true")
	public static boolean ENABLE_EVENT_WINDOW;
	
	//Boost Event.
	@Property(key = "gameserver.boost.event.id", defaultValue = "0")
	public static int BOOST_EVENT_ID;

	@Property(key = "gameserver.boost.event.value", defaultValue = "130")
	public static int BOOST_EVENT_VALUE;

	//Shugo Sweep.
	@Property(key = "gameserver.lucky.dice.season", defaultValue = "3")
	public static int EVENT_LUCK_DICE_SEASON;
}