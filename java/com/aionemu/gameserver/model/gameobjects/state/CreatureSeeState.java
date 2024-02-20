package com.aionemu.gameserver.model.gameobjects.state;

public enum CreatureSeeState
{
	NORMAL(0),
	SEARCH1(1),
	SEARCH2(2),
	SEARCH3(3),
	SEARCH4(4),
	SEARCH5(5),
	SEARCH6(6),
	SEARCH7(7),
	SEARCH8(8),
	SEARCH9(9),
	SEARCH10(10),
	ADMIN(128);
	
	private int id;
	
	private CreatureSeeState(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}