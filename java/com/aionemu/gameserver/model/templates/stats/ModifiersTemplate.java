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

import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @author xavier
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "modifiers")
public class ModifiersTemplate {

	@XmlElements({ 
		@XmlElement(name = "sub", type = com.aionemu.gameserver.model.stats.calc.functions.StatSubFunction.class),
		@XmlElement(name = "add", type = com.aionemu.gameserver.model.stats.calc.functions.StatAddFunction.class),
		@XmlElement(name = "rate", type = com.aionemu.gameserver.model.stats.calc.functions.StatRateFunction.class),
		@XmlElement(name = "set", type = com.aionemu.gameserver.model.stats.calc.functions.StatSetFunction.class) })
	private List<StatFunction> modifiers;

	@XmlAttribute
	private float chance = 100;
	
	@XmlAttribute
	private int level;

	public List<StatFunction> getModifiers() {
		return modifiers;
	}

	public float getChance() {
		return chance;
	}
	
	public float getLevel() {
		return level;
	}
}
