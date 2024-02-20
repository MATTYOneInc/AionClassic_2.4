/*
 *  Aion Classic Emu based on Aion Encom Source Files
 *
 *  ENCOM Team based on Aion-Lighting Open Source
 *  All Copyrights : "Data/Copyrights/AEmu-Copyrights.text
 *
 *  iMPERIVM.FUN - AION DEVELOPMENT FORUM
 *  Forum: <http://https://imperivm.fun/>
 *
 */
package com.aionemu.gameserver.model.templates.materials;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.Player;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Rolandas
 */
@XmlType(name = "MaterialTarget")
@XmlEnum
public enum MaterialTarget {

	ALL,
	NPC,
	PLAYER,
	PLAYER_WITH_PET;

	public String value() {
		return name();
	}

	public static MaterialTarget fromValue(String value) {
		return valueOf(value);
	}

	public boolean isTarget(Creature creature) {
		if (this == ALL)
			return true;
		if (this == NPC)
			return creature instanceof Npc;
		if (this == PLAYER)
			return creature instanceof Player;
		if (this == PLAYER_WITH_PET)
			return creature instanceof Player || creature instanceof Summon && ((Summon) creature).getMaster() != null;
		return false;
	}

}
