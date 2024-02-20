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
package com.aionemu.gameserver.ai2.handler;

import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.geo.GeoService;

/**
 * @author ATracer
 */
public class CreatureEventHandler {

	/**
	 * @param npcAI
	 * @param creature
	 */
	public static void onCreatureMoved(NpcAI2 npcAI, Creature creature) {
		checkAggro(npcAI, creature);
		if (creature instanceof Player) {
			Player player = (Player) creature;
			QuestEngine.getInstance().onAtDistance(new QuestEnv(npcAI.getOwner(), player, 0, 0));
			if (npcAI.getOwner().getPosition().isInstanceMap()) {
				npcAI.getOwner().getPosition().getWorldMapInstance().getInstanceHandler().checkDistance(player, npcAI.getOwner());
			} else {
				npcAI.getOwner().getPosition().getWorld().getWorldMap(npcAI.getOwner().getWorldId()).getWorldHandler().checkDistance(player, npcAI.getOwner());
			}
		}
	}

	/**
	 * @param npcAI
	 * @param creature
	 */
	public static void onCreatureSee(NpcAI2 npcAI, Creature creature) {
		checkAggro(npcAI, creature);
		if (creature instanceof Player) {
			Player player = (Player) creature;
			QuestEngine.getInstance().onAtDistance(new QuestEnv(npcAI.getOwner(), player, 0, 0));
			if (npcAI.getOwner().getPosition().isInstanceMap()) {
				npcAI.getOwner().getPosition().getWorldMapInstance().getInstanceHandler().checkDistance(player, npcAI.getOwner());
			} else {
				npcAI.getOwner().getPosition().getWorld().getWorldMap(npcAI.getOwner().getWorldId()).getWorldHandler().checkDistance(player, npcAI.getOwner());
			}
		}
	}


	/**
	 * @param ai
	 * @param creature
	 */
	protected static void checkAggro(NpcAI2 ai, Creature creature) {
		Npc owner = ai.getOwner();
		
		if (ai.isInState(AIState.FIGHT))
			return;
		
		if (creature.getLifeStats().isAlreadyDead())
			return;

		if (!owner.canSee(creature))
			return;

		if (!owner.getActiveRegion().isMapRegionActive())
			return;

		boolean isInAggroRange = false;
		if (ai.poll(AIQuestion.CAN_SHOUT)) {
			int shoutRange = owner.getObjectTemplate().getMinimumShoutRange();
			double distance = MathUtil.getDistance(owner, creature);
			if (distance <= shoutRange) {
				ShoutEventHandler.onSee(ai, creature);
				isInAggroRange = shoutRange <= owner.getObjectTemplate().getAggroRange();
			}
		}
		if (!ai.isInState(AIState.FIGHT) && (isInAggroRange || MathUtil.isIn3dRange(owner, creature, owner.getObjectTemplate().getAggroRange()))) {
			if (owner.isAggressiveTo(creature) && GeoService.getInstance().canSee(owner, creature)) {
				if (!ai.isInState(AIState.RETURNING))
					ai.getOwner().getMoveController().storeStep();
				if (ai.canThink())
					ai.onCreatureEvent(AIEventType.CREATURE_AGGRO, creature);
			}
		}
	}
}