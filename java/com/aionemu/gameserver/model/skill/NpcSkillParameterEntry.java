package com.aionemu.gameserver.model.skill;

class NpcSkillParameterEntry extends NpcSkillEntry {

	public NpcSkillParameterEntry(int skillId, int skillLevel) {
		super(skillId, skillLevel);
	}

	@Override
	public boolean isReady(int hpPercentage, long fightingTimeInMSec) {
		return true;
	}

	@Override
	public boolean chanceReady() {
		return true;
	}

	@Override
	public boolean hpReady(int hpPercentage) {
		return true;
	}

	@Override
	public boolean timeReady(long fightingTimeInMSec) {
		return true;
	}

	@Override
	public boolean hasCooldown() {
		return false;
	}

	@Override
	public boolean UseInSpawned() {
		return true;
	}
}
