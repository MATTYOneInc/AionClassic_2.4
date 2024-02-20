package com.aionemu.gameserver.model.instance.playerreward;

public class TiakPlayerReward extends InstancePlayerReward
{
	private int zoneCaptured;
	
	public TiakPlayerReward(Integer object) {
		super(object);
	}
	
	public void captureZone() {
		zoneCaptured++;
	}
	
	public int getZoneCaptured() {
		return zoneCaptured;
	}
}