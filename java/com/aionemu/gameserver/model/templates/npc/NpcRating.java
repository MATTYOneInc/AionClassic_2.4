package com.aionemu.gameserver.model.templates.npc;

import com.aionemu.gameserver.model.gameobjects.state.CreatureSeeState;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "rating")
@XmlEnum
public enum NpcRating
{
	JUNK(CreatureSeeState.NORMAL, 0.5f),
	NORMAL(CreatureSeeState.NORMAL, 1f),
	ELITE(CreatureSeeState.SEARCH1, 5f),
	HERO(CreatureSeeState.SEARCH2, 7.5f),
	LEGENDARY(CreatureSeeState.SEARCH2, 10f);
	
	private final CreatureSeeState congenitalSeeState;
	private float modifier;
	
	private NpcRating(CreatureSeeState congenitalSeeState, float modifier) {
		this.congenitalSeeState = congenitalSeeState;
		this.modifier = modifier;
	}
	
	public CreatureSeeState getCongenitalSeeState() {
		return congenitalSeeState;
	}

	public float getModifier() {
		return modifier;
	}
}