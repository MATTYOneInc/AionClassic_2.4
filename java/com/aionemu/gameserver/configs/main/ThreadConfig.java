package com.aionemu.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class ThreadConfig
{
	@Property(key = "gameserver.thread.basepoolsize", defaultValue = "1")
	public static int BASE_THREAD_POOL_SIZE;
	@Property(key = "gameserver.thread.threadpercore", defaultValue = "4")
	public static int EXTRA_THREAD_PER_CORE;
	@Property(key = "gameserver.thread.runtime", defaultValue = "5000")
	public static long MAXIMUM_RUNTIME_IN_MILLISEC_WITHOUT_WARNING;
	@Property(key = "gameserver.thread.usepriority", defaultValue = "false")
	public static boolean USE_PRIORITIES;
	public static int THREAD_POOL_SIZE;
	
	public static void load() {
		final int extraThreadPerCore = EXTRA_THREAD_PER_CORE;
		THREAD_POOL_SIZE = (BASE_THREAD_POOL_SIZE + extraThreadPerCore) * Runtime.getRuntime().availableProcessors();
	}
}