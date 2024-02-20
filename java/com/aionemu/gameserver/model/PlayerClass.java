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
package com.aionemu.gameserver.model;

import com.aionemu.gameserver.model.gameobjects.player.Player;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum PlayerClass
{
	WARRIOR(0, true),
	GLADIATOR(1),
	TEMPLAR(2),
	SCOUT(3, true),
	ASSASSIN(4),
	RANGER(5),
	MAGE(6, true),
	SORCERER(7),
	SPIRIT_MASTER(8),
	PRIEST(9, true),
	CLERIC(10),
	CHANTER(11),
	MONK(12, true),
	THUNDERER(13),
	ALL(14);
	
	private byte classId;
	private int idMask;
	private boolean startingClass;

	private PlayerClass(int classId) {
		this(classId, false);
	}

	private PlayerClass(int classId, boolean startingClass) {
		this.classId = (byte) classId;
		this.startingClass = startingClass;
		this.idMask = (int) Math.pow(2, classId);
	}

	public byte getClassId() {
		return classId;
	}

	public static PlayerClass getPlayerClassById(byte classId) {
		for (PlayerClass pc : values()) {
			if (pc.getClassId() == classId)
				return pc;
		}
		throw new IllegalArgumentException("There is no player class with id " + classId);
	}

	public boolean isStartingClass() {
		return startingClass;
	}

	public static PlayerClass getStartingClassFor(PlayerClass pc) {
		switch (pc) {
			case ASSASSIN:
			case RANGER:
				return SCOUT;
			case GLADIATOR:
			case TEMPLAR:
				return WARRIOR;
			case CHANTER:
			case CLERIC:
				return PRIEST;
			case SORCERER:
			case SPIRIT_MASTER:
				return MAGE;
			case THUNDERER:
				return MONK;
			case SCOUT:
			case WARRIOR:
			case PRIEST:
			case MAGE:
			case MONK:
				return pc;
			default:
				throw new IllegalArgumentException("Given player class is starting class: " + pc);
		}
	}

	public static PlayerClass getPlayerClassByString(String fieldName) {
		for (PlayerClass pc: values()) {
			if (pc.toString().equals(fieldName))
				return pc;
		}
		return null;
	}

	public int getMask() {
		return idMask;
	}

	public String getClassType(Player player) {
		String type = null;
		switch (player.getPlayerClass()) {
			case RANGER:
			case CHANTER:
			case TEMPLAR:
			case ASSASSIN:
			case GLADIATOR:
			case THUNDERER:
				type = "PHYSICAL";
			break;
			case CLERIC:
			case SORCERER:
			case SPIRIT_MASTER:
				type = "MAGICAL";
			break;
			default: break;
		}
		return type;
	}
}