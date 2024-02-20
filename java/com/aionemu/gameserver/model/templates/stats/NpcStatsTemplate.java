package com.aionemu.gameserver.model.templates.stats;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "npc_stats_template")
public class NpcStatsTemplate extends StatsTemplate
{
	@XmlAttribute(name = "run_speed_fight")
	private float runSpeedFight;

	@XmlAttribute(name = "hp_regen")
	private int hpRegen;

	@XmlAttribute(name = "physical_attack")
	private int physicalAttack;

	@XmlAttribute(name = "min_damage")
	private int minDamage;

	@XmlAttribute(name = "max_damage")
	private int maxDamage;

	@XmlAttribute(name = "hit_accuracy")
	private int hitAccuracy;

	@XmlAttribute(name = "magical_attack")
	private int magicalAttack;

	@XmlAttribute(name = "magical_hit_accuracy")
	private int magicalHitAccuracy;

	@XmlAttribute(name = "physical_defend")
	private int physicalDefend;

	@XmlAttribute(name = "magical_resist")
	private int magicalResist;

	@XmlAttribute(name = "critical")
	private int critical;

	@XmlAttribute(name = "magical_critical")
	private int magicalCritical;

	@XmlAttribute(name = "exp")
	private int maxXp;

	@XmlAttribute(name = "dp")
	private int dp;

	@XmlAttribute(name = "ap")
	private int ap;
	
	@XmlAttribute(name = "physical_critical_reduce_rate")
	private int physicalCriticalReduceRate;

	@XmlAttribute(name = "physical_critical_damage_reduce")
	private int physicalCriticalDamageReduce;

	@XmlAttribute(name = "magical_critical_reduce_rate")
	private int magicalCriticalReduceRate;

	@XmlAttribute(name = "magical_critical_damage_reduce")
	private int magicalCriticalDamageReduce;

	@XmlAttribute(name = "concentration")
	private int concentration;
	
	public float getRunSpeedFight() {
		return runSpeedFight;
	}

	public int getMaxXp() {
		return maxXp;
	}

	public int getHpRegen() {
		return hpRegen;
	}

	public int getDp() {
		return dp;
	}

	public int getAp() {
		return ap;
	}

	public int getPhysicalAttack() {
		return physicalAttack;
	}

	public int getMaxDamage() {
		return maxDamage;
	}

	public int getMinDamage() {
		return minDamage;
	}

	public int getHitAccuracy() {
		return hitAccuracy;
	}

	public int getMagicalHitAccuracy() {
		return magicalHitAccuracy;
	}

	public int getMagicalAttack() {
		return magicalAttack;
	}

	public int getPhysicalDefend() {
		return physicalDefend;
	}

	public int getMagicalResist() {
		return magicalResist;
	}

	public int getCritical() {
		return critical;
	}

	public int getMagicalCritical() {
		return magicalCritical;
	}
	
	public int getPhysicalCriticalReduceRate() {
		return physicalCriticalReduceRate;
	}

	public int getPhysicalCriticalDamageReduce() {
		return physicalCriticalDamageReduce;
	}

	public int getMagicalCriticalReduceRate() {
		return magicalCriticalReduceRate;
	}

    public int getMagicalCriticalDamageReduce() {
		return magicalCriticalDamageReduce;
	}

	public int getConcentration() {
		return concentration;
	}
}