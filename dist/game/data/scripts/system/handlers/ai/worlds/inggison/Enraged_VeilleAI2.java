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
package ai.worlds.inggison;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AttackIntention;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.handler.*;
import com.aionemu.gameserver.ai2.manager.*;
import com.aionemu.gameserver.ai2.poll.*;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.NpcSkillEntry;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.*;
import com.aionemu.gameserver.world.knownlist.Visitor;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("enraged_veille")
public class Enraged_VeilleAI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
	private int curentPercent = 100;
	private AtomicBoolean isAggred = new AtomicBoolean(false);
	private List<Integer> percents = new ArrayList<Integer>();
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	@Override
	public void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isAggred.compareAndSet(false, true)) {
			announceAgentUnderAttack();
        }
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{100, 80, 50, 30, 10});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 100:
					    ///Aid me, honorable warrior of Elysea!
						sendMsg(1500122, getObjectId(), false, 0);
						///All prayers to me!
						sendMsg(1500123, getObjectId(), false, 5000);
						///It is time for us to defeat the enemy! Give all your strength to me!
						sendMsg(1500124, getObjectId(), false, 10000);
					break;
					case 80:
					    rndSpawn(281434, 1); //Fire Spirit.
					    rndSpawn(281437, 1); //Undertow.
						///Terror and death to the accursed intruders!
						sendMsg(1500125, getObjectId(), false, 0);
						///Be terrified forever!
						sendMsg(1500126, getObjectId(), false, 5000);
					break;
					case 50:
						rndSpawn(281434, 1); //Fire Spirit.
					    rndSpawn(281435, 1); //Hurrikane.
						announceJusinOdSpawn();
						announceEmpyreanLordAgentHP50();
					break;
					case 30:
					    rndSpawn(281437, 1); //Undertow.
					    rndSpawn(281436, 1); //Earth Spirit.
						///Pay the price with your lives!
						sendMsg(1500127, getObjectId(), false, 0);
						///Still not enough! Prove your determination!
						sendMsg(1500128, getObjectId(), false, 5000);
						///Victory is nigh!
						sendMsg(1500129, getObjectId(), false, 10000);
					break;
					case 10:
					    rndSpawn(281436, 1); //Earth Spirit.
						rndSpawn(281435, 1); //Hurrikane.
					    announceEmpyreanLordAgentHP10();
					break;
				}
				percents.remove(percent);
				break;
			}
		}
	}
	
	private void rndSpawn(int npcId, int count) {
		for (int i = 0; i < count; i++) {
			SpawnTemplate template = rndSpawnInRange(npcId, 10);
			SpawnEngine.spawnObject(template, getPosition().getInstanceId());
		}
	}
	
	protected SpawnTemplate rndSpawnInRange(int npcId, float distance) {
		float direction = Rnd.get(0, 199) / 100f;
		float x = (float) (Math.cos(Math.PI * direction) * distance);
        float y = (float) (Math.sin(Math.PI * direction) * distance);
		return SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), npcId, getPosition().getX() + x, getPosition().getY() + y, getPosition().getZ(), getPosition().getHeading());
	}
	
	private void announceKilledVeille() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				AionObject winner = getAggroList().getMostDamage();
				if (winner instanceof Creature) {
					final Creature kill = (Creature) winner;
					//"Player Name" of the "Race" has killed Kaisinel's Agent Veille.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400324, kill.getRace().getRaceDescriptionId(), kill.getName()));
				}
			}
		});
	}
	
	private void announceAgentUnderAttack() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//Kaisinel's Agent Veille is under attack!
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_FIELDABYSS_LIGHTBOSS_ATTACKED, 0);
			}
		});
	}
	
	private void announceJusinOdSpawn() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//The Empyrean Lord Agent summoned the Aether Concentrator.
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_LDF4_Jusin_OdSpawn, 0);
				//The Empyrean Lord Agent has enabled the Aether Concentrator.
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_LDF4_Jusin_OdStart, 20000);
			}
		});
	}
	
	private void announceEmpyreanLordAgentHP50() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//The Empyrean Lord Agent's HP has dropped below 50%
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_LDF4_Jusin_Hp50, 0);
			}
		});
	}
	
	private void announceEmpyreanLordAgentHP10() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//The Empyrean Lord Agent's HP has dropped below 10%
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_MSG_LDF4_Jusin_Hp10, 0);
			}
		});
	}
	
	private void announceDespawn() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//Kaisinel's Agent Veille has disappeared.
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_FIELDABYSS_LIGHTBOSS_DESPAWN, 0);
			}
		});
	}
	
	private void killNpc(List<Npc> npcs) {
		for (Npc npc: npcs) {
			AI2Actions.killSilently(this, npc);
		}
	}
	
	@Override
    protected void handleDespawned() {
        super.handleDespawned();
		announceDespawn();
		percents.clear();
    }
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		addPercent();
	}
	
	@Override
    protected void handleBackHome() {
        super.handleBackHome();
		addPercent();
		canThink = true;
		curentPercent = 100;
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281434));
		killNpc(instance.getNpcs(281435));
		killNpc(instance.getNpcs(281436));
		killNpc(instance.getNpcs(281437));
    }
	
	@Override
    protected void handleDied() {
        super.handleDied();
		percents.clear();
		announceKilledVeille();
		///Lord Kaisinel... please help us...
		sendMsg(1500130, getObjectId(), false, 0);
		///It isn't over yet... Lord Kaisinel will come to our aid...
		sendMsg(1500131, getObjectId(), false, 3000);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281434));
		killNpc(instance.getNpcs(281435));
		killNpc(instance.getNpcs(281436));
		killNpc(instance.getNpcs(281437));
    }
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
	
	@Override
	public void think() {
		ThinkEventHandler.onThink(this);
	}
	
	@Override
    protected void handleCreatureSee(Creature creature) {
        CreatureEventHandler.onCreatureSee(this, creature);
    }
	
	@Override
	protected void handleCreatureAggro(Creature creature) {
		if (canThink()) {
		    AggroEventHandler.onAggro(this, creature);
		}
	}
	
	@Override
	protected void handleFinishAttack() {
		AttackEventHandler.onFinishAttack(this);
	}
	
	@Override
	protected void handleAttackComplete() {
		AttackEventHandler.onAttackComplete(this);
	}
	
	@Override
    protected void handleTargetGiveup() {
        TargetEventHandler.onTargetGiveup(this);
    }
	
    @Override
    protected void handleTargetChanged(Creature creature) {
        TargetEventHandler.onTargetChange(this, creature);
    }
	
	@Override
	protected boolean handleGuardAgainstAttacker(Creature attacker) {
		return AggroEventHandler.onGuardAgainstAttacker(this, attacker);
	}
	
	@Override
	protected boolean handleCreatureNeedsSupport(Creature creature) {
		return AggroEventHandler.onCreatureNeedsSupport(this, creature);
	}
	
	@Override
	protected AIAnswer pollInstance(AIQuestion question) {
		switch (question) {
			case SHOULD_DECAY:
			    return AIAnswers.POSITIVE;
			case SHOULD_REWARD:
			    return AIAnswers.POSITIVE;
			case SHOULD_RESPAWN:
			    return AIAnswers.POSITIVE;
			case CAN_RESIST_ABNORMAL:
			    return AIAnswers.POSITIVE;
			case CAN_ATTACK_PLAYER:
			    return AIAnswers.POSITIVE;
			default:
				return null;
		}
	}
	
	@Override
	public AttackIntention chooseAttackIntention() {
		VisibleObject currentTarget = getTarget();
		Creature mostHated = getAggroList().getMostHated();
		if (mostHated == null || mostHated.getLifeStats().isAlreadyDead()) {
			return AttackIntention.FINISH_ATTACK;
		} if (currentTarget == null || !currentTarget.getObjectId().equals(mostHated.getObjectId())) {
			onCreatureEvent(AIEventType.TARGET_CHANGED, mostHated);
			return AttackIntention.SWITCH_TARGET;
		} if (getOwner().getObjectTemplate().getAttackRange() == 0) {
			NpcSkillEntry skill = getOwner().getSkillList().getRandomSkill();
			if (skill != null) {
				skillId = skill.getSkillId();
				skillLevel = skill.getSkillLevel();
				return AttackIntention.SKILL_ATTACK;
			}
		} else {
			NpcSkillEntry skill = SkillAttackManager.chooseNextSkill(this);
			if (skill != null) {
				skillId = skill.getSkillId();
				skillLevel = skill.getSkillLevel();
				return AttackIntention.SKILL_ATTACK;
			}
		}
		return AttackIntention.SIMPLE_ATTACK;
	}
}