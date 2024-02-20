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

public class CustomConfig
{
	@Property(key = "gameserver.premium.notify", defaultValue = "false")
	public static boolean PREMIUM_NOTIFY;
	@Property(key = "gameserver.enchant.announce.enable", defaultValue = "true")
	public static boolean ENABLE_ENCHANT_ANNOUNCE;
	@Property(key = "gameserver.chat.factions.enable", defaultValue = "false")
	public static boolean SPEAKING_BETWEEN_FACTIONS;
	@Property(key = "gameserver.chat.whisper.level", defaultValue = "10")
	public static int LEVEL_TO_WHISPER;
	@Property(key = "gameserver.search.factions.mode", defaultValue = "false")
	public static boolean FACTIONS_SEARCH_MODE;
	@Property(key = "gameserver.search.gm.list", defaultValue = "false")
	public static boolean SEARCH_GM_LIST;
	@Property(key = "gameserver.cross.faction.binding", defaultValue = "false")
	public static boolean ENABLE_CROSS_FACTION_BINDING;
	@Property(key = "gameserver.simple.secondclass.enable", defaultValue = "false")
	public static boolean ENABLE_SIMPLE_2NDCLASS;
	@Property(key = "gameserver.skill.chain.triggerrate", defaultValue = "true")
	public static boolean SKILL_CHAIN_TRIGGERRATE;
	@Property(key = "gameserver.unstuck.delay", defaultValue = "3600")
	public static int UNSTUCK_DELAY;
	@Property(key = "gameserver.admin.dye.price", defaultValue = "1000000")
	public static int DYE_PRICE;
	@Property(key = "gameserver.base.flytime", defaultValue = "60")
	public static int BASE_FLYTIME;
	@Property(key = "gameserver.oldnames.coupon.disable", defaultValue = "false")
	public static boolean OLD_NAMES_COUPON_DISABLED;
	@Property(key = "gameserver.oldnames.command.disable", defaultValue = "true")
	public static boolean OLD_NAMES_COMMAND_DISABLED;
	@Property(key = "gameserver.friendlist.size", defaultValue = "90")
	public static int FRIENDLIST_SIZE;
	@Property(key = "gameserver.basic.questsize.limit", defaultValue = "38")
	public static int BASIC_QUEST_SIZE_LIMIT;
	@Property(key = "gameserver.instances.enable", defaultValue = "true")
	public static boolean ENABLE_INSTANCES;
	@Property(key = "gameserver.instances.mob.aggro", defaultValue = "300030000,300040000,300050000,300060000,300070000,300080000,300090000,300100000,300110000,300120000,300130000,300140000,300150000,300160000,300170000,300190000,300200000,300210000,300220000,300230000,300250000,300300000,300320000,300350000,300360000,300420000,300430000,310010000,310020000,310030000,310040000,310050000,310060000,310070000,310080000,310090000,310100000,310110000,310120000,320010000,320020000,320030000,320040000,320050000,320060000,320070000,320080000,320090000,320100000,320110000,320120000,320130000,320140000,320150000")
	public static String INSTANCES_MOB_AGGRO;
	@Property(key = "gameserver.instances.cooldown.filter", defaultValue = "0")
	public static String INSTANCES_COOL_DOWN_FILTER;
	@Property(key = "gameserver.instances.cooldown.rate", defaultValue = "1")
	public static int INSTANCES_RATE;
	@Property(key = "gameserver.enable.kinah.cap", defaultValue = "false")
	public static boolean ENABLE_KINAH_CAP;
	@Property(key = "gameserver.kinah.cap.value", defaultValue = "1000000000")
	public static long KINAH_CAP_VALUE;
	@Property(key = "gameserver.noap.mentor.group", defaultValue = "false")
	public static boolean MENTOR_GROUP_AP;
	@Property(key = "gameserver.faction.free", defaultValue = "true")
	public static boolean FACTION_FREE_USE;
	@Property(key = "gameserver.faction.prices", defaultValue = "10000")
	public static int FACTION_USE_PRICE;
	@Property(key = "gameserver.faction.cmdchannel", defaultValue = "true")
	public static boolean FACTION_CMD_CHANNEL;
	@Property(key = "gameserver.dialog.show.id", defaultValue = "true")
	public static boolean ENABLE_SHOW_DIALOG_ID;
	@Property(key = "gameserver.reward.service.enable", defaultValue = "false")
	public static boolean ENABLE_REWARD_SERVICE;
	@Property(key = "gameserver.limits.enable", defaultValue = "true")
	public static boolean LIMITS_ENABLED;
	@Property(key = "gameserver.limits.update", defaultValue = "0 0 0 ? * *")
	public static String LIMITS_UPDATE;
    @Property(key = "gameserver.limits.rate", defaultValue="1")
    public static int LIMITS_RATE;
    @Property(key = "gameserver.chat.text.length", defaultValue="150")
    public static int MAX_CHAT_TEXT_LENGHT;
    @Property(key = "gameserver.abyssxform.afterlogout", defaultValue="false")
    public static boolean ABYSSXFORM_LOGOUT;
    @Property(key = "gameserver.instance.duel.enable", defaultValue="true")
    public static boolean INSTANCE_DUEL_ENABLE;
	@Property(key = "gameserver.commands.admin.dot.enable", defaultValue = "false")
	public static boolean ENABLE_ADMIN_DOT_COMMANDS;
	@Property(key = "gameserver.rift.enable", defaultValue = "true")
	public static boolean RIFT_ENABLED;
	@Property(key = "gameserver.rift.duration", defaultValue = "1")
	public static int RIFT_DURATION;
	@Property(key = "gameserver.rift.appear.chance", defaultValue = "50")
    public static int RIFT_APPEAR_CHANCE;
	
	//Instance Rift
	@Property(key = "gameserver.instance.rift.enable", defaultValue = "true")
	public static boolean INSTANCE_RIFT_ENABLED;
	@Property(key = "gameserver.instance.rift.duration", defaultValue = "8")
	public static int INSTANCE_RIFT_DURATION;
	
	//Serial Killer
	@Property(key = "gameserver.serial.killer.enable", defaultValue = "true")
	public static boolean SERIAL_KILLER_ENABLED;
	@Property(key = "gameserver.serial.killer.handled.worlds", defaultValue = "")
	public static String SERIAL_KILLER_WORLDS = "";
	@Property(key = "gameserver.serial.killer.kills.refresh", defaultValue = "5")
	public static int SERIAL_KILLER_REFRESH;
	@Property(key = "gameserver.serial.killer.kills.decrease", defaultValue = "1")
	public static int SERIAL_KILLER_DECREASE;
	@Property(key = "gameserver.serial.killer.level.diff", defaultValue = "10")
	public static int SERIAL_KILLER_LEVEL_DIFF;
	@Property(key = "gameserver.serial.killer.1st.rank.kills", defaultValue = "25")
	public static int KILLER_1ST_RANK_KILLS;
	@Property(key = "gameserver.serial.killer.2nd.rank.kills", defaultValue = "50")
	public static int KILLER_2ND_RANK_KILLS;

	//Discord BOT
	@Property(key = "gameserver.discord.bot.enable", defaultValue = "true")
	public static boolean DISCORD_BOT_ENABLED;
	@Property(key = "gameserver.discord.bot.token", defaultValue = "")
	public static String DISCORD_BOT_TOKEN;
	@Property(key = "gameserver.discord.bot.game", defaultValue = "")
	public static String DISCORD_BOT_GAME;
}