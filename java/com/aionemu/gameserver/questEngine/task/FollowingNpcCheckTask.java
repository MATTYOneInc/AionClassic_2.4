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
package com.aionemu.gameserver.questEngine.task;

import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.utils.MathUtil;

/**
 * @author ATracer
 */
public class FollowingNpcCheckTask implements Runnable {

	private final QuestEnv env;
	private final DestinationChecker destinationChecker;

	/**
	 * @param player
	 * @param npc
	 * @param destinationChecker
	 */
	FollowingNpcCheckTask(QuestEnv env, DestinationChecker destinationChecker) {
		this.env = env;
		this.destinationChecker = destinationChecker;
	}

	@Override
	public void run() {
		final Player player = env.getPlayer();
		final Npc npc = (Npc) env.getVisibleObject();
		if (player.getLifeStats().isAlreadyDead() || npc.getLifeStats().isAlreadyDead()) {
			onFail(env);
		}
		if (!MathUtil.isIn3dRange(player, npc, 50)) {
			onFail(env);
		}

		if (destinationChecker.check()) {
			onSuccess(env);
		}
	}

	/**
	 * Following task succeeded, proceed with quest
	 */
	private final void onSuccess(QuestEnv env) {
		stopFollowing(env);
		QuestEngine.getInstance().onNpcReachTarget(env);
	}

	/**
	 * Following task failed, abort further progress
	 */
	protected void onFail(QuestEnv env) {
		stopFollowing(env);
		QuestEngine.getInstance().onNpcLostTarget(env);
	}

	private final void stopFollowing(QuestEnv env) {
		Player player = env.getPlayer();
		Npc npc = (Npc) env.getVisibleObject();
		player.getController().cancelTask(TaskId.QUEST_FOLLOW);
		npc.getAi2().onCreatureEvent(AIEventType.STOP_FOLLOW_ME, player);
	}
}
