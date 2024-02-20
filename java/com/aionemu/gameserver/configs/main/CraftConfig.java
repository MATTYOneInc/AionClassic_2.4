package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class CraftConfig
{
    @Property(key = "gameserver.craft.skills.unrestricted.levelup.enable", defaultValue = "false")
    public static boolean UNABLE_CRAFT_SKILLS_UNRESTRICTED_LEVELUP;
    @Property(key = "gameserver.craft.max.expert.skills", defaultValue = "2")
    public static int MAX_EXPERT_CRAFTING_SKILLS;
    @Property(key = "gameserver.craft.max.master.skills", defaultValue = "1")
    public static int MAX_MASTER_CRAFTING_SKILLS;
    @Property(key = "gameserver.craft.critical.rate.regular", defaultValue = "15")
    public static int CRAFT_CRIT_RATE;
    @Property(key = "gameserver.craft.critical.rate.premium", defaultValue = "15")
    public static int PREMIUM_CRAFT_CRIT_RATE;
    @Property(key = "gameserver.craft.critical.rate.vip", defaultValue = "15")
    public static int VIP_CRAFT_CRIT_RATE;
    @Property(key = "gameserver.craft.combo.rate.regular", defaultValue = "25")
    public static int CRAFT_COMBO_RATE;
    @Property(key = "gameserver.craft.combo.rate.premium", defaultValue = "25")
    public static int PREMIUM_CRAFT_COMBO_RATE;
    @Property(key = "gameserver.craft.combo.rate.vip", defaultValue = "25")
    public static int VIP_CRAFT_COMBO_RATE;
	public static boolean CRAFT_CHECK_TASK = false;
    @Property(key = "gameserver.craft.chance.purple.crit", defaultValue = "1")
    public static int CRAFT_CHANCE_PURPLE_CRIT;
    @Property(key = "gameserver.craft.chance.blue.crit", defaultValue = "10")
    public static int CRAFT_CHANCE_BLUE_CRIT;
    @Property(key = "gameserver.craft.chance.instant", defaultValue = "100")
    public static int CRAFT_CHANCE_INSTANT;
}