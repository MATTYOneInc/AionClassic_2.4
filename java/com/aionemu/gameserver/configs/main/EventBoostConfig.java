package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class EventBoostConfig {

    @Property(key = "gameserver.boost.event.enable", defaultValue = "true")
    public static boolean BOOST_EVENT_ENABLE;

    @Property(key = "gameserver.boost.event.value", defaultValue = "150")
    public static int BOOST_EVENT_VALUE;

}
