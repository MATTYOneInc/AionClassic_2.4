package com.aionemu.gameserver.skillengine.model;

public enum SkillMoveType
{
	DODGE(0),
    RESIST(1),
    DEFAULT(16),
    PULL(18),
    STUMBLE(20),
    KNOCKBACK(28),
    MOVEBEHIND(48),
    STAGGER(112),
    NEWPULL(54);
	
	private int id;
	
	private SkillMoveType(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}