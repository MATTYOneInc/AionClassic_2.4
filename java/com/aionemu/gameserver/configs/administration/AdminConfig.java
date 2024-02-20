package com.aionemu.gameserver.configs.administration;

import com.aionemu.commons.configuration.Property;

public class AdminConfig
{
	@Property(key = "gameserver.administration.gmlevel", defaultValue = "3")
	public static int GM_LEVEL;
	@Property(key = "gameserver.administration.gmpanel", defaultValue = "3")
	public static int GM_PANEL;
	@Property(key = "gameserver.administration.baseshield", defaultValue = "3")
	public static int COMMAND_BASESHIELD;
	@Property(key = "gameserver.administration.flight.freefly", defaultValue = "3")
	public static int GM_FLIGHT_FREE;
	@Property(key = "gameserver.administration.flight.unlimited", defaultValue = "3")
	public static int GM_FLIGHT_UNLIMITED;
	@Property(key = "gameserver.administration.doors.opening", defaultValue = "3")
	public static int DOORS_OPEN;
	@Property(key = "gameserver.administration.auto.res", defaultValue = "3")
	public static int ADMIN_AUTO_RES;
	@Property(key = "gameserver.administration.instancereq", defaultValue = "3")
	public static int INSTANCE_REQ;
	@Property(key = "gameserver.administration.view.player", defaultValue = "3")
	public static int ADMIN_VIEW_DETAILS;
	@Property(key = "gameserver.administration.invis.gm.connection", defaultValue = "false")
	public static boolean INVISIBLE_GM_CONNECTION;
	@Property(key = "gameserver.administration.enemity.gm.connection", defaultValue = "Normal")
	public static String ENEMITY_MODE_GM_CONNECTION;
	@Property(key = "gameserver.administration.invul.gm.connection", defaultValue = "false")
	public static boolean INVULNERABLE_GM_CONNECTION;
	@Property(key = "gameserver.administration.vision.gm.connection", defaultValue = "false")
	public static boolean VISION_GM_CONNECTION;
	@Property(key = "gameserver.administration.whisper.gm.connection", defaultValue = "false")
	public static boolean WHISPER_GM_CONNECTION;
	@Property(key = "gameserver.administration.quest.dialog.log", defaultValue = "false")
	public static boolean QUEST_DIALOG_LOG;
	@Property(key = "gameserver.administration.trade.item.restriction", defaultValue = "false")
	public static boolean ENABLE_TRADEITEM_RESTRICTION;
	/**
	 * GM TAG
	 */
	@Property(key = "gameserver.admin.tag.enable", defaultValue="false")
    public static boolean ADMIN_TAG_ENABLE;
	@Property(key = "gameserver.admin.tag.1", defaultValue = "\uE06F Supporter \uE06F %s")
	public static String ADMIN_TAG_1;
	@Property(key = "gameserver.admin.tag.2", defaultValue = "\uE04E Junior-GM \uE04E %s")
	public static String ADMIN_TAG_2;
	@Property(key = "gameserver.admin.tag.3", defaultValue = "\uE050 Senior-GM \uE050 %s")
	public static String ADMIN_TAG_3;
	@Property(key = "gameserver.admin.tag.4", defaultValue = "\uE04A Head-GM \uE04A %s")
	public static String ADMIN_TAG_4;
	@Property(key = "gameserver.admin.tag.5", defaultValue = "\uE0BD Admin \uE0BD %s")
	public static String ADMIN_TAG_5;
	@Property(key = "gameserver.admin.tag.6", defaultValue = "\uE0BD Developer \uE0BD %s")
	public static String ADMIN_TAG_6;
	@Property(key = "gameserver.admin.tag.7", defaultValue = "\uE0BD Server-CoAdmin \uE0BD %s")
	public static String ADMIN_TAG_7;
	@Property(key = "gameserver.admin.tag.8", defaultValue = "\uE0BD Server-Admin \uE0BD %s")
	public static String ADMIN_TAG_8;
	@Property(key = "gameserver.admin.tag.9", defaultValue = "\uE0BD Server-CoOwner \uE0BD %s")
	public static String ADMIN_TAG_9;
	@Property(key = "gameserver.admin.tag.10", defaultValue = "\uE0BD Server-Owner \uE0BD %s")
	public static String ADMIN_TAG_10;
	@Property(key = "gameserver.admin.announce.levels", defaultValue = "*")
	public static String ANNOUNCE_LEVEL_LIST;
	@Property(key = "gameserver.administration.enchant.gm.nofail", defaultValue = "false")
	public static boolean GM_ENCHANT_NOFAIL;
}