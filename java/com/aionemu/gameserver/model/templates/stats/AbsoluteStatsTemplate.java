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

import javax.xml.bind.annotation.*;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StatsSet", propOrder = { "modifiers" })
public class AbsoluteStatsTemplate {

	@XmlElement(required = true)
	protected ModifiersTemplate modifiers;

	@XmlAttribute(required = true)
	protected int id;

	public ModifiersTemplate getModifiers() {
		return this.modifiers;
	}

	/**
	 * Gets the value of the id property.
	 */
	public int getId() {
		return id;
	}
}
