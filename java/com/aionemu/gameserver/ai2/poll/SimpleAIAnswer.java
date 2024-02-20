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
package com.aionemu.gameserver.ai2.poll;

/**
 * @author ATracer
 */
public class SimpleAIAnswer implements AIAnswer {

	private final boolean answer;

	/**
	 * @param answer
	 */
	SimpleAIAnswer(boolean answer) {
		this.answer = answer;
	}

	@Override
	public boolean isPositive() {
		return answer;
	}

	@Override
	public Object getResult() {
		return answer;
	}

}
