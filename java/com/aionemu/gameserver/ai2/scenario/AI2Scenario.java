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
package com.aionemu.gameserver.ai2.scenario;

import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * @author ATracer
 */
public interface AI2Scenario {

	void onCreatureEvent(AbstractAI ai, AIEventType event, Creature creature);

	void onGeneralEvent(AbstractAI ai, AIEventType event);
}
