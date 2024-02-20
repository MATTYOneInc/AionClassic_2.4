package com.aionemu.gameserver.model;

public class RankCount
{
	private int playerId;
	private int ap;
	private int gp;
	private Race race;
	
	public RankCount(int playerId, int ap, int gp, Race race) {
		this.playerId = playerId;
		this.ap = ap;
		this.gp = gp;
		this.race = race;
	}
	
	public int getPlayerId() {
		return playerId;
	}
	
	public int getPlayerAP() {
		return ap;
	}
	
	public int getPlayerGP() {
		return gp;
	}
	
	public Race getPlayerRace() {
		return race;
	}
}