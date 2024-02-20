package com.aionemu.gameserver.skillengine.model;

public enum SpellStatus
{
	NONE(0),
	STUMBLE(1),
	STAGGER(2),
	OPENAERIAL(4),
	CLOSEAERIAL(8),
	SPIN(16),
	BLOCK(32),
	PARRY(64),
	DODGE(128),
	DODGE2(-128),
	RESIST(256),
	SNARE(512),
	//????????
	STUN(1024),
	SLOW(2048),
	SLEEP(4096),
	SILENCE(8192);
	
	private int id;
	
	private SpellStatus(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}