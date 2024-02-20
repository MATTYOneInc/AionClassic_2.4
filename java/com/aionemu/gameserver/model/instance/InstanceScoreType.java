package com.aionemu.gameserver.model.instance;

public enum InstanceScoreType
{
	PREPARING(1 * 1024 * 1024),
	START_PROGRESS(2 * 1024 * 1024),
	END_PROGRESS(3 * 1024 * 1024);
	
	private int id;
	
	private InstanceScoreType(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean isPreparing() {
		return id == 1048576;
	}
	
	public boolean isStartProgress() {
		return id == 2097152;
	}
	
	public boolean isEndProgress() {
		return id == 3145728;
	}
}