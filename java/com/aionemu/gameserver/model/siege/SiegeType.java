package com.aionemu.gameserver.model.siege;

public enum SiegeType
{
	FORTRESS(0),
	ARTIFACT(1),
	BOSSRAID_LIGHT(2),
	BOSSRAID_DARK(3),
	INDUN(4),
	UNDERPASS(5),
	SOURCE(6),
	TOWER(7);
	
	private int typeId;
	
	private SiegeType(int id) {
		this.typeId = id;
	}
	
	public int getTypeId() {
		return this.typeId;
	}
}