package com.aionemu.gameserver.model.gameobjects;

public enum NpcObjectType
{
	NORMAL(1),
	SUMMON(2),
	HOMING(16),
	TRAP(32),
	SKILLAREA(64),
	TOTEM(128),
	GROUPGATE(256),
	SERVANT(1024),
	PET(2048);
	
	private NpcObjectType(int id) {
		this.id = id;
	}
	
	private int id;
	
	public int getId() {
		return id;
	}
}