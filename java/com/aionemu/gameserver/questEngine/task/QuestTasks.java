package com.aionemu.gameserver.questEngine.task;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.spawns.SpawnSearchResult;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.zone.ZoneName;

import java.util.concurrent.Future;

public class QuestTasks
{
	public static final Future<?> newFollowingToTargetCheckTask(final QuestEnv env, Npc npc, Npc target) {
		return ThreadPoolManager.getInstance().scheduleAtFixedRate(new FollowingNpcCheckTask(env, new TargetDestinationChecker(npc, target)), 1000, 1000);
	}
	
	public static final Future<?> newFollowingToTargetCheckTask(final QuestEnv env, Npc npc, int npcTargetId) {
		SpawnSearchResult searchResult = DataManager.SPAWNS_DATA2.getFirstSpawnByNpcId(npc.getWorldId(), npcTargetId);
		if (searchResult == null) {
			throw new IllegalArgumentException("Supplied npc doesn't exist: " + npcTargetId);
		}
		return ThreadPoolManager.getInstance().scheduleAtFixedRate(new FollowingNpcCheckTask(env, new CoordinateDestinationChecker(npc, searchResult.getSpot().getX(), searchResult.getSpot().getY(), searchResult.getSpot().getZ())), 1000, 1000);
	}
	
	public static final Future<?> newFollowingToTargetCheckTask(final QuestEnv env, Npc npc, float x, float y, float z) {
		return ThreadPoolManager.getInstance().scheduleAtFixedRate(new FollowingNpcCheckTask(env, new CoordinateDestinationChecker(npc, x, y, z)), 1000, 1000);
	}
	
	public static final Future<?> newFollowingToTargetCheckTask(final QuestEnv env, Npc npc, ZoneName zoneName) {
		return ThreadPoolManager.getInstance().scheduleAtFixedRate(new FollowingNpcCheckTask(env, new ZoneChecker(npc, zoneName)), 1000, 1000);
	}
}