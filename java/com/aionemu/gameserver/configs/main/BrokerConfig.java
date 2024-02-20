/*
 *  Aion Classic Emu based on Aion Encom Source Files
 *
 *  ENCOM Team based on Aion-Lighting Open Source
 *  All Copyrights : "Data/Copyrights/AEmu-Copyrights.text
 *
 *  iMPERIVM.FUN - AION DEVELOPMENT FORUM
 *  Forum: <http://https://imperivm.fun/>
 *
 */
package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class BrokerConfig
{
    @Property(key = "gameserver.broker.save.manager.interval", defaultValue = "6")
    public static int SAVE_MANAGER_INTERVAL;
    @Property(key = "gameserver.broker.time.check.expired.items.interval", defaultValue = "60")
    public static int CHECK_EXPIRED_ITEMS_INTERVAL;
    @Property(key = "gameserver.broker.antihack.punishment", defaultValue = "0")
    public static int ANTI_HACK_PUNISHMENT;
    @Property(key = "gameserver.broker.items.expiretime", defaultValue = "8")
    public static int ITEMS_EXPIRE_TIME;
}