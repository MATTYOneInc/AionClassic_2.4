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
package com.aionemu.gameserver.model.templates.quest;

import com.aionemu.gameserver.model.templates.rewards.BonusType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Rolandas
 *
 */

/**
 * <p>
 * Java class for QuestBonuses complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="QuestBonuses">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="type" use="required" type="{}BonusType" />
 *       &lt;attribute name="level" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="skill" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestBonuses")
public class QuestBonuses {

	@XmlAttribute(required = true)
	protected BonusType type;
	@XmlAttribute
	protected Integer level;
	@XmlAttribute
	protected Integer skill;

	/**
	 * Gets the value of the type property.
	 * 
	 * @return possible object is {@link BonusType }
	 */
	public BonusType getType() {
		return type;
	}

	/**
	 * Gets the value of the level property.
	 * 
	 * @return possible object is {@link Integer }
	 */
	public Integer getLevel() {
		return level;
	}

	/**
	 * Gets the value of the skill property.
	 * 
	 * @return possible object is {@link Integer }
	 */
	public Integer getSkill() {
		return skill;
	}
}
