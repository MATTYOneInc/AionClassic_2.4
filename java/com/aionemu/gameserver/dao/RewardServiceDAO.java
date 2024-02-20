package com.aionemu.gameserver.dao;

import com.aionemu.gameserver.model.templates.rewards.RewardEntryItem;
import javolution.util.FastList;

public class RewardServiceDAO
{
	public static FastList<RewardEntryItem> getAvailable(int playerId)
	{
		// unused
		return new FastList<>();
	}

	public static void uncheckAvailable(FastList<Integer> ids)
	{
		// unused
	}

	public static void setUpdateDown(int unique)
	{
		// unused
	}

	public static boolean setUpdate(int unique)
	{
		// unused
		return false;
	}
}
