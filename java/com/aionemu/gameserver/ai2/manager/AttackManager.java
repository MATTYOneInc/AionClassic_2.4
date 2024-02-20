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
package com.aionemu.gameserver.ai2.manager;

import com.aionemu.gameserver.ai2.AI2Logger;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.AttackIntention;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;

public class AttackManager
{
	public static void startAttacking(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "AttackManager: startAttacking");
		}
		npcAI.getOwner().getGameStats().setFightStartingTime();
		EmoteManager.emoteStartAttacking(npcAI.getOwner());
		scheduleNextAttack(npcAI);
	}
	
	public static void scheduleNextAttack(NpcAI2 npcAI) {
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "AttackManager: scheduleNextAttack");
		}
		AISubState subState = npcAI.getSubState();
		if (subState == AISubState.NONE) {
			chooseAttack(npcAI, npcAI.getOwner().getGameStats().getNextAttackInterval());
		} else {
			if (npcAI.isLogging()) {
				AI2Logger.info(npcAI, "Will not choose attack in substate" + subState);
			}
		}
	}
	
	protected static void chooseAttack(NpcAI2 npcAI, int delay) {
		AttackIntention attackIntention = npcAI.chooseAttackIntention();
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "AttackManager: chooseAttack " + attackIntention + " delay " + delay);
		} if (!npcAI.canThink()) {
			return;
		} if (npcAI.getOwner().getGameStats().getAttackRange().getCurrent() == 0) {
            attackIntention = AttackIntention.SKILL_ATTACK;
        } if (DataManager.SKILL_DATA.getSkillTemplate(npcAI.getSkillId()) == null) {
            attackIntention = AttackIntention.SIMPLE_ATTACK;
        } switch (attackIntention) {
			case SIMPLE_ATTACK:
				SimpleAttackManager.performAttack(npcAI, delay);
			break;
			case SKILL_ATTACK:
				SkillAttackManager.performAttack(npcAI, delay);
			break;
			case FINISH_ATTACK:
				npcAI.think();
			break;
			default:
            break;
		}
	}
	
	public static void targetTooFar(NpcAI2 npcAI) {
		Npc npc = npcAI.getOwner();
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "AttackManager: attackTimeDelta " + npc.getGameStats().getLastAttackTimeDelta());
		} if (npc.getGameStats().getLastChangeTargetTimeDelta() > 5) {
			Creature mostHated = npc.getAggroList().getMostHated();
			if (mostHated != null && !mostHated.getLifeStats().isAlreadyDead() && !npc.isTargeting(mostHated.getObjectId())) {
				if (npcAI.isLogging()) {
					AI2Logger.info(npcAI, "AttackManager: switching target during chase");
				}
				npcAI.onCreatureEvent(AIEventType.TARGET_CHANGED, mostHated);
				return;
			}
		} if (!npc.canSee((Creature) npc.getTarget())) {
			npcAI.onGeneralEvent(AIEventType.TARGET_GIVEUP);
			return;
		} if (checkGiveupDistance(npcAI)) {
			npcAI.onGeneralEvent(AIEventType.TARGET_GIVEUP);
			return;
		} if (npcAI.isMoveSupported()) {
			npc.getMoveController().moveToTargetObject();
			return;
		}
		npcAI.onGeneralEvent(AIEventType.TARGET_GIVEUP);
	}
	
	private static boolean checkGiveupDistance(NpcAI2 npcAI) {
		Npc npc = npcAI.getOwner();
		float distanceToTarget = npc.getDistanceToTarget();
		if (npcAI.isLogging()) {
			AI2Logger.info(npcAI, "AttackManager: distanceToTarget " + distanceToTarget);
		}
		int chaseTarget = npc.isBoss() ? 50 : npc.getPosition().getWorldMapInstance().getTemplate().getAiInfo().getChaseTarget();
		if (distanceToTarget > chaseTarget) {
			return true;
		}
		double distanceToHome = npc.getDistanceToSpawnLocation();
		int chaseHome = npc.isBoss() ? 150 : npc.getPosition().getWorldMapInstance().getTemplate().getAiInfo().getChaseHome();
		if (distanceToHome > chaseHome) {
			return true;
		} if (chaseHome <= 200) {
			if ((npc.getGameStats().getLastAttackTimeDelta() > 20 && npc.getGameStats().getLastAttackedTimeDelta() > 20) ||
			    (distanceToHome > chaseHome / 2 && npc.getGameStats().getLastAttackedTimeDelta() > 10)) {
				return true;
			}
		}
		return false;
	}
}