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
package com.aionemu.gameserver.model.stats.container;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AI2Logger;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.SummonedObject;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.stats.calc.Stat2;
import com.aionemu.gameserver.model.templates.stats.NpcStatsTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.S_ACTION;
import com.aionemu.gameserver.taskmanager.tasks.PacketBroadcaster.BroadcastMode;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author xavier
 */
public class NpcGameStats extends CreatureGameStats<Npc> {

	int currentRunSpeed = 0;
	private long lastAttackTime = 0;
	private long lastAttackedTime = 0;
	private long nextAttackTime = 0;
	private long lastSkillTime = 0;
	private long fightStartingTime = 0;
	private int cachedState;
	private Stat2 cachedSpeedStat;
	private long lastGeoZUpdate;
	private long lastChangeTarget = 0;
        
	public NpcGameStats(Npc owner) {
		super(owner);
	}

	@Override
	protected void onStatsChange() {
		checkSpeedStats();
	}
	
	private void checkSpeedStats() {
		Stat2 oldSpeed = cachedSpeedStat;
		cachedSpeedStat = null;
		Stat2 newSpeed = getMovementSpeed();
		cachedSpeedStat = newSpeed;
		if (oldSpeed == null || oldSpeed.getCurrent() != newSpeed.getCurrent()) {
			owner.addPacketBroadcastMask(BroadcastMode.UPDATE_SPEED);
		}
	}

	@Override
	public Stat2 getMaxHp() {
		return getStat(StatEnum.MAXHP, owner.getObjectTemplate().getStatsTemplate().getMaxHp());
	}
	@Override
	public Stat2 getMaxMp() {
		return getStat(StatEnum.MAXMP, owner.getObjectTemplate().getStatsTemplate().getMaxHp());
	}
	@Override
	public Stat2 getAttackSpeed() {
		return getStat(StatEnum.ATTACK_SPEED, owner.getObjectTemplate().getAttackDelay());
	}
	public Stat2 getStrikeResist() {
		return getStat(StatEnum.PHYSICAL_CRITICAL_RESIST, 0);
	}
	public Stat2 getStrikeFort() {
        return getStat(StatEnum.PHYSICAL_CRITICAL_DAMAGE_REDUCE, 0);
    }
	public Stat2 getSpellResist() {
		return getStat(StatEnum.MAGICAL_CRITICAL_RESIST, 0);
	}
    public Stat2 getSpellFort() {
        return getStat(StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE, 0);
    }
    public Stat2 getBCastingTime() {
        return getStat(StatEnum.BOOST_CASTING_TIME, 1000);
    }
	public Stat2 getConcentration() {
        return getStat(StatEnum.CONCENTRATION, ((Npc)this.owner).getObjectTemplate().getStatsTemplate().getConcentration());
    }
	public Stat2 getRootResistance() {
        return getStat(StatEnum.ROOT_RESISTANCE, 0);
    }
	public Stat2 getSnareResistance() {
        return getStat(StatEnum.SNARE_RESISTANCE, 0);
    }
	public Stat2 getBindResistance() {
        return getStat(StatEnum.BIND_RESISTANCE, 0);
    }
	public Stat2 getFearResistance() {
        return getStat(StatEnum.FEAR_RESISTANCE, 0);
    }
	public Stat2 getSleepResistance() {
        return getStat(StatEnum.SLEEP_RESISTANCE, 0);
    }
	@Override
	public Stat2 getMainHandMAccuracy() {
		if (owner instanceof SummonedObject) {
			return getStat(StatEnum.MAGICAL_ACCURACY, owner.getObjectTemplate().getStatsTemplate().getMagicalHitAccuracy());
		}
		return getMainHandPAccuracy();
	}
	public Stat2 getAllSpeed() {
		return getStat(StatEnum.ALLSPEED, 7500);
	}
	public Stat2 getPhysicalCriticalReduceRate() {
		return getStat(StatEnum.PHYSICAL_CRITICAL_REDUCE_RATE, ((Npc)this.owner).getObjectTemplate().getStatsTemplate().getPhysicalCriticalReduceRate());
	}
	public Stat2 getMagicalCriticalReduceRate() {
		return getStat(StatEnum.MAGICAL_CRITICAL_REDUCE_RATE, ((Npc)this.owner).getObjectTemplate().getStatsTemplate().getMagicalCriticalReduceRate());
	}
	public Stat2 getPhysicalCriticalDamageReduce() {
		return getStat(StatEnum.PHYSICAL_CRITICAL_DAMAGE_REDUCE, ((Npc)this.owner).getObjectTemplate().getStatsTemplate().getPhysicalCriticalDamageReduce());
	}
	public Stat2 getMagicalCriticalDamageReduce() {
		return getStat(StatEnum.MAGICAL_CRITICAL_DAMAGE_REDUCE, ((Npc)this.owner).getObjectTemplate().getStatsTemplate().getMagicalCriticalDamageReduce());
	}

	@Override
	public Stat2 getMovementSpeed() {
		int currentState = owner.getState();
		Stat2 cachedSpeed = cachedSpeedStat;
		if (cachedSpeed != null && cachedState == currentState) {
			return cachedSpeed;
		}
		Stat2 newSpeedStat = null;
		if (owner.isFlying()) {
			newSpeedStat = getStat(StatEnum.FLY_SPEED, Math.round(owner.getObjectTemplate().getStatsTemplate().getRunSpeed() * 1.3f * 1000.0f));
		} else if (owner.isInState(CreatureState.WEAPON_EQUIPPED)) {
			newSpeedStat = getStat(StatEnum.SPEED, Math.round(owner.getObjectTemplate().getStatsTemplate().getRunSpeedFight() * 1000.0f));
		} else if (owner.isInState(CreatureState.WALKING)) {
			newSpeedStat = getStat(StatEnum.SPEED, Math.round(owner.getObjectTemplate().getStatsTemplate().getWalkSpeed() * 1000.0f));
		} else {
			newSpeedStat = getStat(StatEnum.SPEED, Math.round(owner.getObjectTemplate().getStatsTemplate().getRunSpeed() * 1000.0f));
		}
		cachedState = currentState;
		cachedSpeedStat = newSpeedStat;
		return newSpeedStat;
	}

	@Override
	public Stat2 getAttackRange() {
		return getStat(StatEnum.ATTACK_RANGE, owner.getObjectTemplate().getAttackRange() * 1500);
	}

	@Override
	public Stat2 getPDef() {
		return getStat(StatEnum.PHYSICAL_DEFENSE, owner.getObjectTemplate().getStatsTemplate().getPhysicalDefend());
	}

	@Override
	public Stat2 getMDef() {
		return getStat(StatEnum.MAGICAL_DEFEND, 0);
	}

	@Override
	public Stat2 getMResist() {
		return getStat(StatEnum.MAGICAL_RESIST, owner.getObjectTemplate().getStatsTemplate().getMagicalResist());
	}

	@Override
	public Stat2 getMBResist() {
		int base = 0;
		return getStat(StatEnum.MAGIC_SKILL_BOOST_RESIST, base);
	}

	@Override
	public Stat2 getPower() {
		return getStat(StatEnum.POWER, 100);
	}

	@Override
	public Stat2 getHealth() {
		return getStat(StatEnum.HEALTH, 100);
	}

	@Override
	public Stat2 getAccuracy() {
		return getStat(StatEnum.ACCURACY, 100);
	}

	@Override
	public Stat2 getAgility() {
		return getStat(StatEnum.AGILITY, 100);
	}

	@Override
	public Stat2 getKnowledge() {
		return getStat(StatEnum.KNOWLEDGE, 100);
	}

	@Override
	public Stat2 getWill() {
		return getStat(StatEnum.WILL, 100);
	}

	@Override
	public Stat2 getEvasion() {
		return getStat(StatEnum.EVASION, owner.getObjectTemplate().getStatsTemplate().getEvasion());
	}

	@Override
	public Stat2 getParry() {
		return getStat(StatEnum.PARRY, owner.getObjectTemplate().getStatsTemplate().getParry());
	}

	@Override
	public Stat2 getBlock() {
		return getStat(StatEnum.BLOCK, owner.getObjectTemplate().getStatsTemplate().getBlock());
	}

	@Override
	public Stat2 getMainHandPAttack() {
		return getStat(StatEnum.PHYSICAL_ATTACK, owner.getObjectTemplate().getStatsTemplate().getPhysicalAttack());
	}

	@Override
	public Stat2 getMainHandPCritical() {
		return getStat(StatEnum.PHYSICAL_CRITICAL, owner.getObjectTemplate().getStatsTemplate().getCritical());
	}

	@Override
	public Stat2 getMainHandPAccuracy() {
		return getStat(StatEnum.PHYSICAL_ACCURACY, owner.getObjectTemplate().getStatsTemplate().getHitAccuracy());
	}

	@Override
	public Stat2 getMAttack() {
		return getStat(StatEnum.MAGICAL_ATTACK, owner.getObjectTemplate().getStatsTemplate().getMagicalAttack());
	}

	@Override
	public Stat2 getMainHandMAttack() {
		return getStat(StatEnum.MAGICAL_ATTACK, owner.getObjectTemplate().getStatsTemplate().getMagicalAttack());
	}

	@Override
	public Stat2 getOffHandMAttack() {
		return getStat(StatEnum.MAGICAL_ATTACK, 0);
	}

	@Override
	public Stat2 getMBoost() {
		return getStat(StatEnum.BOOST_MAGICAL_SKILL, 100);
	}

	@Override
	public Stat2 getMAccuracy() {
		if (owner instanceof SummonedObject) {
			return getStat(StatEnum.MAGICAL_ACCURACY, owner.getObjectTemplate().getStatsTemplate().getMagicalHitAccuracy());
		}
		return getMainHandPAccuracy();
	}

	@Override
	public Stat2 getMCritical() {
		return getStat(StatEnum.MAGICAL_CRITICAL, owner.getObjectTemplate().getStatsTemplate().getMagicalCritical());
	}

	@Override
	public Stat2 getHpRegenRate() {
		NpcStatsTemplate nst = owner.getObjectTemplate().getStatsTemplate();
		return getStat(StatEnum.REGEN_HP, nst.getHpRegen());
	}

	@Override
	public Stat2 getMpRegenRate() {
		throw new IllegalStateException("No mp regen for NPC");
	}

	public int getLastAttackTimeDelta() {
		return Math.round((System.currentTimeMillis() - lastAttackTime) / 1000f);
	}

	public int getLastAttackedTimeDelta() {
		return Math.round((System.currentTimeMillis() - lastAttackedTime) / 1000f);
	}

	public void renewLastAttackTime() {
		this.lastAttackTime = System.currentTimeMillis();
	}

	public void renewLastAttackedTime() {
		this.lastAttackedTime = System.currentTimeMillis();
	}

	public boolean isNextAttackScheduled() {
		return nextAttackTime - System.currentTimeMillis() > 50;
	}

    public void setFightStartingTime() {
        this.fightStartingTime = System.currentTimeMillis();
    }

    public long getFightStartingTime() {
        return this.fightStartingTime;
    }

	public void setNextAttackTime(long nextAttackTime) {
		this.nextAttackTime = nextAttackTime;
	}

	public int getNextAttackInterval() {
		long attackDelay = System.currentTimeMillis() - lastAttackTime;
		int attackSpeed = getAttackSpeed().getCurrent();
		if (attackSpeed == 0) {
			attackSpeed = 2000;
		} if (owner.getAi2().isLogging()) {
			AI2Logger.info(owner.getAi2(), "adelay = " + attackDelay + " aspeed = " + attackSpeed);
		}
		int nextAttack = 0;
		if (attackDelay < attackSpeed) {
			nextAttack = (int) (attackSpeed - attackDelay);
		}
		return nextAttack;
	}

	public void renewLastSkillTime() {
		this.lastSkillTime = System.currentTimeMillis();
	}

	public void renewLastChangeTargetTime() {
		this.lastChangeTarget = System.currentTimeMillis();
	}

	public int getLastSkillTimeDelta() {
		return Math.round((System.currentTimeMillis() - lastSkillTime) / 1000f);
	}

	public int getLastChangeTargetTimeDelta() {
		return Math.round((System.currentTimeMillis() - lastChangeTarget) / 1000f);
	}

	public boolean canUseNextSkill() {
		return this.getLastSkillTimeDelta() >= 6 + Rnd.get((int)-3, (int)3);
	}

	@Override
	public void updateSpeedInfo() {
		PacketSendUtility.broadcastPacket(owner, new S_ACTION(owner, EmotionType.START_EMOTE2, 0, 0));
	}

	public final long getLastGeoZUpdate() {
		return lastGeoZUpdate;
	}

	public void setLastGeoZUpdate(long lastGeoZUpdate) {
		this.lastGeoZUpdate = lastGeoZUpdate;
	}
}