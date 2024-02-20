package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class PvPConfig
{

  @Property(key="gameserver.pvp.chainkill.time.restriction", defaultValue="0")
  public static int CHAIN_KILL_TIME_RESTRICTION;

  @Property(key="gameserver.pvp.chainkill.number.restriction", defaultValue="30")
  public static int CHAIN_KILL_NUMBER_RESTRICTION;

  @Property(key="gameserver.pvp.max.leveldiff.restriction", defaultValue="9")
  public static int MAX_AUTHORIZED_LEVEL_DIFF;

  @Property(key="gameserver.pvp.medal.rewarding.enable", defaultValue="false")
  public static boolean ENABLE_MEDAL_REWARDING;

  @Property(key="gameserver.pvp.medal.reward.chance", defaultValue="10")
  public static float MEDAL_REWARD_CHANCE;

  @Property(key="gameserver.pvp.medal.reward.quantity", defaultValue="1")
  public static int MEDAL_REWARD_QUANTITY;

  @Property(key="gameserver.pvp.toll.rewarding.enable", defaultValue="false")
  public static boolean ENABLE_TOLL_REWARDING;

  @Property(key="gameserver.pvp.toll.reward.chance", defaultValue="50")
  public static float TOLL_REWARD_CHANCE;

  @Property(key="gameserver.pvp.toll.reward.quantity", defaultValue="1")
  public static int TOLL_REWARD_QUANTITY;

  @Property(key="gameserver.pvp.killingspree.enable", defaultValue="false")
  public static boolean ENABLE_KILLING_SPREE_SYSTEM;

  @Property(key="gameserver.pvp.raw.killcount.spree", defaultValue="20")
  public static int SPREE_KILL_COUNT;

  @Property(key="gameserver.pvp.raw.killcount.rampage", defaultValue="35")
  public static int RAMPAGE_KILL_COUNT;

  @Property(key="gameserver.pvp.raw.killcount.genocide", defaultValue="50")
  public static int GENOCIDE_KILL_COUNT;

  @Property(key="gameserver.pvp.special_reward.type", defaultValue="0")
  public static int GENOCIDE_SPECIAL_REWARDING;

  @Property(key="gameserver.pvp.special_reward.chance", defaultValue="2")
  public static float SPECIAL_REWARD_CHANCE;
}