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
package ai.worlds.gelkmaros;

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

@AIName("enraged_mastarius")
public class Enraged_MastariusAI2 extends AggressiveNpcAI2
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
					    ///Help me, heroes of Asmodae!
						sendMsg(1500132, getObjectId(), false, 0);
						///Pray for victory!
						sendMsg(1500133, getObjectId(), false, 5000);
						///We must stop the enemy! Give your strength to me!
						sendMsg(1500134, getObjectId(), false, 10000);
					break;
					case 80:
					    rndSpawn(282890, 1); //Fire Spirit.
					    rndSpawn(282893, 1); //Undertow.
					    ///This is the end for the intruders!
						sendMsg(1500135, getObjectId(), false, 0);
						///This is the end! Be gone!
						sendMsg(1500136, getObjectId(), false, 5000);
					break;
					case 50:
					    rndSpawn(282890, 1); //Fire Spirit.
					    rndSpawn(282891, 1); //Hurrikane.
						announceJusinOdSpawn();
						announceEmpyreanLordAgentHP50();
					break;
					case 30:
					    rndSpawn(282893, 1); //Undertow.
					    rndSpawn(282892, 1); //Earth Spirit.
					    ///The scale of victory has tipped to our side.
						sendMsg(1500137, getObjectId(), false, 0);
						///Victory is close! Asmodians, show your strength!
						sendMsg(1500138, getObjectId(), false, 5000);
						///Pray harder! The enemy's end approaches!
						sendMsg(1500139, getObjectId(), false, 10000);
					break;
					case 10:
					    rndSpawn(282892, 1); //Earth Spirit.
						rndSpawn(282891, 1); //Hurrikane.
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
	
	private void announceKilledMarchutan() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				AionObject winner = getAggroList().getMostDamage();
				if (winner instanceof Creature) {
					final Creature kill = (Creature) winner;
					//"Player Name" of the "Race" has killed Marchutan's Agent Mastarius.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400323, kill.getRace().getRaceDescriptionId(), kill.getName()));
				}
			}
		});
	}
	
	private void announceAgentUnderAttack() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				//Marchutan's Agent Mastarius is under attack!
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_FIELDABYSS_DARKBOSS_ATTACKED, 0);
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
				//Marchutan's Agent Mastarius has disappeared.
				PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_FIELDABYSS_DARKBOSS_DESPAWN, 0);
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
		killNpc(instance.getNpcs(282890));
		killNpc(instance.getNpcs(282891));
		killNpc(instance.getNpcs(282892));
		killNpc(instance.getNpcs(282893));
    }
	
	@Override
    protected void handleDied() {
        super.handleDied();
		percents.clear();
		announceKilledMarchutan();
		///Is fate... giving up on me...
		sendMsg(1500140, getObjectId(), false, 0);
		///Lord Marchutan! Please save us!
		sendMsg(1500141, getObjectId(), false, 3000);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(282890));
		killNpc(instance.getNpcs(282891));
		killNpc(instance.getNpcs(282892));
		killNpc(instance.getNpcs(282893));
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