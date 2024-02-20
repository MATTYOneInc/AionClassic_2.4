package com.aionemu.gameserver.model;

public enum NpcType
{
	NULL(-1),
	ATTACKABLE(0),
	PEACE(2),
	AGGRESSIVE(8),
	INVULNERABLE(10),
	NON_ATTACKABLE(38),
	SUPPORT(54);
	
	private int someClientSideId;
	
	private NpcType(int id) {
		this.someClientSideId = id;
	}
	
	public int getId() {
		return someClientSideId;
	}
}