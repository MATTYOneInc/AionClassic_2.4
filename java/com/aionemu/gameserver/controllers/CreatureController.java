package com.aionemu.gameserver.controllers;

import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AI2;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.handler.ShoutEventHandler;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.controllers.attack.AttackResult;
import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Homing;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.ItemAttackType;
import com.aionemu.gameserver.network.aion.serverpackets.S_ATTACK;
import com.aionemu.gameserver.network.aion.serverpackets.S_HIT_POINT_OTHER.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.S_HIT_POINT_OTHER.TYPE;
import com.aionemu.gameserver.network.aion.serverpackets.S_MOVE_NEW;
import com.aionemu.gameserver.network.aion.serverpackets.S_SKILL_CANCELED;
import com.aionemu.gameserver.network.aion.serverpackets.S_NPC_CHANGED_TARGET;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.HealType;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.Skill.SkillMethod;
import com.aionemu.gameserver.taskmanager.tasks.MovementNotifyTask;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneUpdateService;

import javolution.util.FastMap;

public abstract class CreatureController<T extends Creature> extends VisibleObjectController<Creature>
{
	private static final Logger log = LoggerFactory.getLogger(CreatureController.class);
	private FastMap<Integer, Future<?>> tasks = new FastMap<Integer, Future<?>>().shared();
	private int SimpleAttackType;
	
	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange) {
		super.notSee(object, isOutOfRange);
		if (object == getOwner().getTarget()) {
			getOwner().setTarget(null);
			PacketSendUtility.broadcastPacket(getOwner(), new S_NPC_CHANGED_TARGET(getOwner()));
		}
	}
	
	public void onStartMove() {
		getOwner().getObserveController().notifyMoveObservers();
		notifyAIOnMove();
	}
	
	public void onMove() {
		notifyAIOnMove();
	}
	
	public void onStopMove() {
		notifyAIOnMove();
	}
	
	public void onReturnHome() {
	}
	
	protected void notifyAIOnMove() {
		MovementNotifyTask.getInstance().add(getOwner());
	}
	
	public void refreshZoneImpl() {
		getOwner().revalidateZones();
	}
	
	public final void updateZone() {
		ZoneUpdateService.getInstance().add(getOwner());
	}
	
	public void onEnterZone(ZoneInstance zoneInstance) {
	}
	
	public void onLeaveZone(ZoneInstance zoneInstance) {
	}
	
	public void onDie(Creature lastAttacker) {
		this.getOwner().setCasting(null);
		this.getOwner().getMoveController().abortMove();
		this.getOwner().getController().cancelCurrentSkill();
		this.getOwner().getEffectController().removeAllEffects();
		if (getOwner() instanceof Player) {
			if (((Player) getOwner()).getIsFlyingBeforeDeath()) {
				getOwner().unsetState(CreatureState.ACTIVE);
				getOwner().setState(CreatureState.FLOATING_CORPSE);
			} else {
				this.getOwner().setState(CreatureState.DEAD);
			}
		} else {
			if (getOwner() instanceof Npc) {
				if (((Npc) getOwner()).getObjectTemplate().isFloatCorpse()) {
					getOwner().setState(CreatureState.FLOATING_CORPSE);
				}
			}
			this.getOwner().setState(CreatureState.DEAD);
		}
		this.getOwner().getObserveController().notifyDeathObservers(lastAttacker);
	}
	
	public void onDieSilence(Creature lastAttacker) {
		this.getOwner().setCasting(null);
		this.getOwner().getMoveController().abortMove();
		this.getOwner().getController().cancelCurrentSkill();
		this.getOwner().getEffectController().removeAllEffects();
		if (getOwner() instanceof Player) {
			if (((Player) getOwner()).getIsFlyingBeforeDeath()) {
				getOwner().unsetState(CreatureState.ACTIVE);
				getOwner().setState(CreatureState.FLOATING_CORPSE);
			} else {
				this.getOwner().setState(CreatureState.DEAD);
			}
		} else {
			if (getOwner() instanceof Npc) {
				if (((Npc) getOwner()).getObjectTemplate().isFloatCorpse()) {
					getOwner().setState(CreatureState.FLOATING_CORPSE);
				}
			}
			this.getOwner().setState(CreatureState.DEAD);
		}
		this.getOwner().getObserveController().notifyDeathObservers(lastAttacker);
	}
	
	public void onAttack(final Creature attacker, int skillId, TYPE type, int damage, boolean notifyAttack, LOG log) {
		if (damage != 0 && !((getOwner() instanceof Npc) && ((Npc) getOwner()).isBoss()) || !notifyAttack) {
			Skill skill = getOwner().getCastingSkill();
			if (skill != null && log != LOG.BLEED && log != LOG.SPELLATK && log != LOG.POISON) {
				if (skill.getSkillMethod() == SkillMethod.ITEM) {
					cancelCurrentSkill();
				} else {
					int cancelRate = skill.getSkillTemplate().getCancelRate();
					if (cancelRate == 100000) {
						cancelCurrentSkill();
					} else if (cancelRate > 0) {
						int conc = getOwner().getGameStats().getStat(StatEnum.CONCENTRATION, 0).getCurrent();
						float maxHp = getOwner().getGameStats().getMaxHp().getCurrent();
						float cancel = ((7.0f * (damage / maxHp) * 100.0f) - conc / 2.0f) * (cancelRate / 100.0f);
						if (Rnd.get(100) < cancel) {
							cancelCurrentSkill();
						}
					}
				}
			}
		} if (damage == 0 && getOwner().getEffectController().isUnderShield() || getOwner().getEffectController().isUnderholyShield()) {
            notifyAttack = false;
        } if (damage != 0 && notifyAttack) {
			getOwner().getObserveController().notifyAttackedObservers(attacker);
		} if (damage > getOwner().getLifeStats().getCurrentHp()) {
			damage = getOwner().getLifeStats().getCurrentHp() + 1;
		}
        getOwner().getAggroList().addDamage(attacker, damage);
		getOwner().getLifeStats().reduceHp(damage, attacker);
		if (getOwner() instanceof Npc) {
            AI2 ai = getOwner().getAi2();
            if (ai.poll(AIQuestion.CAN_SHOUT)) {
                if (attacker instanceof Player) {
                    ShoutEventHandler.onHelp((NpcAI2) ai, attacker);
                } else {
                    ShoutEventHandler.onEnemyAttack((NpcAI2) ai, attacker);
                }
            }
        } else if (getOwner() instanceof Player && attacker instanceof Npc) {
			AI2 ai = attacker.getAi2();
			if (ai.poll(AIQuestion.CAN_SHOUT)) {
				ShoutEventHandler.onAttack((NpcAI2) ai, getOwner());
			}
		}
        getOwner().incrementAttackedCount();
		getOwner().getKnownList().doOnAllNpcs(new Visitor<Npc>() {
            @Override
            public void visit(Npc object) {
				object.getAi2().onCreatureEvent(AIEventType.CREATURE_NEEDS_SUPPORT, getOwner());
            }
        });
    }
	
	public final void onAttack(Creature creature, int skillId, final int damage, boolean notifyAttack) {
		this.onAttack(creature, skillId, TYPE.REGULAR, damage, notifyAttack, LOG.REGULAR);
	}
	
	public final void onAttack(Creature creature, final int damage, boolean notifyAttack) {
		this.onAttack(creature, 0, TYPE.REGULAR, damage, notifyAttack, LOG.REGULAR);
	}
	
	public void onRestore(HealType hopType, int value) {
		switch (hopType) {
			case HP:
				getOwner().getLifeStats().increaseHp(TYPE.HP, value);
			break;
			case MP:
				getOwner().getLifeStats().increaseMp(TYPE.MP, value);
			break;
			case FP:
				getOwner().getLifeStats().increaseFp(TYPE.FP, value);
			break;
		}
	}
	
	public void doDrop(Player player) {
	}
	
	public void doReward() {
	}
	
	public void onDialogRequest(Player player) {
	}
	
	public int getSimpleAttackType() {
		return this.SimpleAttackType;
	}
	
	public void setSimpleAttackType(int attackType) {
		this.SimpleAttackType = attackType;
	}
	
	public void attackTarget(final Creature target, int attackNo, int time, int type) {
		boolean addAttackObservers = true;
		if (target == null || !getOwner().canAttack() || getOwner().getLifeStats().isAlreadyDead() || !getOwner().isSpawned()) {
			return;
		}
		int attackType = 0;
		List<AttackResult> attackResult;
		if (getOwner() instanceof Homing) {
			attackResult = AttackUtil.calculateHomingAttackResult(getOwner(), target, getOwner().getAttackType().getMagicalElement());
			attackType = 1;
		} else {
			if (getOwner().getAttackType() == ItemAttackType.PHYSICAL) {
				attackResult = AttackUtil.calculatePhysicalAttackResult(getOwner(), target);
			} else {
				attackResult = AttackUtil.calculateMagicalAttackResult(getOwner(), target, getOwner().getAttackType().getMagicalElement());
				attackType = 1;
			}
		}
		int damage = 0;
		for (AttackResult result : attackResult) {
			if (result.getAttackStatus() == AttackStatus.RESIST || result.getAttackStatus() == AttackStatus.DODGE) {
				addAttackObservers = false;
			}
			damage += result.getDamage();
		}
		PacketSendUtility.broadcastPacketAndReceive(getOwner(), new S_ATTACK(getOwner(), target, attackNo, time, attackType, attackResult));
		if (addAttackObservers) {
			getOwner().getObserveController().notifyAttackObservers(target);
		}
		final Creature creature = getOwner();
		if (time == 0) {
			target.getController().onAttack(getOwner(), damage, true);
		} else {
			ThreadPoolManager.getInstance().schedule(new DelayedOnAttack(target, creature, damage), time);
		}
	}
	
	public void stopMoving() {
		Creature owner = getOwner();
		World.getInstance().updatePosition(owner, owner.getX(), owner.getY(), owner.getZ(), owner.getHeading());
		PacketSendUtility.broadcastPacket(owner, new S_MOVE_NEW(owner));
	}
	
	public void onDialogSelect(int dialogId, Player player, int questId, int extendedRewardIndex) {
	}
	
	public Future<?> getTask(TaskId taskId) {
		return tasks.get(taskId.ordinal());
	}
	
	public boolean hasTask(TaskId taskId) {
		return tasks.containsKey(taskId.ordinal());
	}
	
	public boolean hasScheduledTask(TaskId taskId) {
		Future<?> task = tasks.get(taskId.ordinal());
		return task != null ? !task.isDone() : false;
	}
	
	public Future<?> cancelTask(TaskId taskId) {
		Future<?> task = tasks.remove(taskId.ordinal());
		if (task != null) {
			task.cancel(false);
		}
		return task;
	}
	
	public void addTask(TaskId taskId, Future<?> task) {
		cancelTask(taskId);
		tasks.put(taskId.ordinal(), task);
	}
	
	public void cancelAllTasks() {
		for (int i : tasks.keySet()) {
			Future<?> task = tasks.get(i);
			if (task != null && i != TaskId.RESPAWN.ordinal()) {
				task.cancel(false);
			}
		}
		tasks.clear();
	}
	
	@Override
	public void delete() {
		cancelAllTasks();
		super.delete();
	}
	
	public void die() {
        getOwner().getLifeStats().reduceHp(getOwner().getLifeStats().getCurrentHp() + 1, getOwner());
    }
	
	public final boolean useSkill(int skillId) {
		return useSkill(skillId, 1);
	}
	
	public boolean useSkill(int skillId, int skillLevel) {
		try {
			Creature creature = getOwner();
			Skill skill = SkillEngine.getInstance().getSkill(creature, skillId, skillLevel, creature.getTarget());
			if (skill != null) {
				return skill.useSkill();
			}
		} catch (Exception ex) {
			log.error("Exception during skill use: " + skillId, ex);
		}
		return false;
	}

	public void broadcastHate(int value) {
		for (VisibleObject visibleObject : getOwner().getKnownList().getKnownObjects().values()) {
			if (visibleObject instanceof Creature) {
				((Creature) visibleObject).getAggroList().notifyHate(getOwner(), value);
			}
		}
	}
	
	public void abortCast() {
		Creature creature = getOwner();
		Skill skill = creature.getCastingSkill();
		if (skill == null) {
			return;
		}
		creature.setCasting(null);
		if (creature.getSkillNumber() > 0) {
			creature.setSkillNumber(creature.getSkillNumber() - 1);
		}
	}
	
	public void cancelCurrentSkill() {
		if (getOwner().getCastingSkill() == null) {
			return;
		}
		Creature creature = getOwner();
		Skill castingSkill = creature.getCastingSkill();
		castingSkill.cancelCast();
		creature.removeSkillCoolDown(castingSkill.getSkillTemplate().getDelayId());
		creature.setCasting(null);
		creature.setNextSkillUse(0);
		if (castingSkill.getSkillMethod() == SkillMethod.CAST) {
			PacketSendUtility.broadcastPacket(creature, new S_SKILL_CANCELED(creature, castingSkill.getSkillTemplate().getSkillId()));
		} if (getOwner().getAi2() instanceof NpcAI2) {
			NpcAI2 npcAI = (NpcAI2) getOwner().getAi2();
			npcAI.setSubStateIfNot(AISubState.NONE);
			npcAI.onGeneralEvent(AIEventType.ATTACK_COMPLETE);
			if (creature.getSkillNumber() > 0) {
				creature.setSkillNumber(creature.getSkillNumber() - 1);
			}
		}
	}
	
	public void cancelUseItem() {
	}
	
	@Override
	public void onDespawn() {
		cancelTask(TaskId.DECAY);
		Creature owner = getOwner();
		if (owner == null || !owner.isSpawned()) {
			return;
		}
		owner.getAggroList().clear();
		owner.getObserveController().clear();
	}
	
	private static final class DelayedOnAttack implements Runnable {
		private Creature target;
		private Creature creature;
		private int finalDamage;
		public DelayedOnAttack(Creature target, Creature creature, int finalDamage) {
			this.target = target;
			this.creature = creature;
			this.finalDamage = finalDamage;
		}
		@Override
		public void run() {
			target.getController().onAttack(creature, finalDamage, true);
			target = null;
			creature = null;
		}
	}
	
	@Override
	public void onAfterSpawn() {
		super.onAfterSpawn();
		getOwner().revalidateZones();
	}
}