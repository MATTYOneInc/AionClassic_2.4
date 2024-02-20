package com.aionemu.gameserver.skillengine.model;

public enum DashStatus
{
	NONE(0),
	RANDOMMOVELOC(1),
	DASH(2),
	BACKDASH(3),
	MOVEBEHIND(4);
	
	private int id;
	
	private DashStatus(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}