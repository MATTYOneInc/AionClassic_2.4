package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class RankingConfig
{
	@Property(key = "gameserver.top.ranking.update.setting", defaultValue = "true")
	public static boolean TOP_RANKING_UPDATE_SETTING;
	
	@Property(key = "gameserver.top.ranking.update.hour", defaultValue = "0 0 */2 ? * *")
	public static String TOP_RANKING_UPDATE_RULE;
	
	@Property(key = "gameserver.top.ranking.update.minute", defaultValue = "10")
	public static int TOP_RANKING_UPDATE_RULE2;
	
	@Property(key = "gameserver.top.ranking.max.offline.days", defaultValue = "0")
    public static int TOP_RANKING_MAX_OFFLINE_DAYS;
}