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
package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.skillengine.model.Effect;

/****/
/** Author Rinzler (Encom)
/****/

public class NoDeathPenaltyReduceEffect extends BuffEffect
{
	public void calculate(Effect effect) {
		effect.addSucessEffect(this);
	}
	
	public void startEffect(Effect effect) {
		effect.setNoDeathPenaltyReduce(true);
	}
}