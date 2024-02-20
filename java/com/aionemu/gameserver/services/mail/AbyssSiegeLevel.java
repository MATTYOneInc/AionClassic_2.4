package com.aionemu.gameserver.services.mail;

public enum AbyssSiegeLevel
{
	NONE(0),
	HERO_DECORATION(1),
	MEDAL(2),
	ELITE_SOLDIER(3),
	VETERAN_SOLDIER(4);
	
	private int value;
	
	private AbyssSiegeLevel(int value) {
		this.value = value;
	}
	
	public int getId() {
		return this.value;
	}
}