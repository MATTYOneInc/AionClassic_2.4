package com.aionemu.gameserver.model.gameobjects;

import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.HomingGameStats;
import com.aionemu.gameserver.model.stats.container.NpcLifeStats;
import com.aionemu.gameserver.model.templates.item.ItemAttackType;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import org.apache.commons.lang.StringUtils;

public class Homing extends SummonedObject<Creature>
{
	private int attackCount;
	private int skillId;
	private int activeSkillId;
	
	public Homing(int objId, NpcController controller, SpawnTemplate spawnTemplate, NpcTemplate objectTemplate, int level, int skillId) {
		super(objId, controller, spawnTemplate, objectTemplate, level);
		this.skillId = skillId;
	}
	
	@Override
	protected void setupStatContainers(int level) {
		setGameStats(new HomingGameStats(this));
		setLifeStats(new NpcLifeStats(this));
	}
	
	public void setAttackCount(int attackCount) {
		this.attackCount = attackCount;
	}
	
	public int getAttackCount() {
		return attackCount;
	}
	
	@Override
	public boolean isEnemy(Creature creature) {
		return getCreator().isEnemy(creature);
	}
	
	@Override
	public boolean isEnemyFrom(Player player) {
		return getCreator() != null ? getCreator().isEnemyFrom(player) : false;
	}
	
	@Override
	public NpcObjectType getNpcObjectType() {
		return NpcObjectType.HOMING;
	}
	
	@Override
	public String getMasterName() {
		return StringUtils.EMPTY;
	}
	
	@Override
	public ItemAttackType getAttackType() {
		if ((getName().contains("wind")) || (getName().contains("cyclone"))) {
			return ItemAttackType.MAGICAL_WIND;
		}
		return ItemAttackType.PHYSICAL;
	}
	
	public int getSkillId() {
		return skillId;
	}
	
	public int getActiveSkillId() {
		return activeSkillId;
	}
	
	public void setActiveSkillId(int activeSkillId) {
		this.activeSkillId = activeSkillId;
	}
}