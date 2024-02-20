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
package com.aionemu.gameserver.model.templates.stats;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Luno
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "player_stats_template")
public class PlayerStatsTemplate extends StatsTemplate {

	@XmlAttribute(name = "power")
	private int power;
	@XmlAttribute(name = "health")
	private int health;
	@XmlAttribute(name = "agility")
	private int agility;
	@XmlAttribute(name = "accuracy")
	private int accuracy;
	@XmlAttribute(name = "knowledge")
	private int knowledge;
	@XmlAttribute(name = "will")
	private int will;

	public int getPower() {
		return power;
	}

	public int getHealth() {
		return health;
	}

	public int getAgility() {
		return agility;
	}

	public int getAccuracy() {
		return accuracy;
	}

	public int getKnowledge() {
		return knowledge;
	}

	public int getWill() {
		return will;
	}
}
