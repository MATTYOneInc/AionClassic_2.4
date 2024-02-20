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
package com.aionemu.gameserver.model.stats.calc;

import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;

/**
 * @author ATracer
 */
public interface StatCondition {

	/**
	 * Validate that function should be applied to the stat
	 * 
	 * @param stat
	 * @param statFunction
	 * @return
	 */
	boolean validate(Stat2 stat, IStatFunction statFunction);
}
