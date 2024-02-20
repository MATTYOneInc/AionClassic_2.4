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
package ai.instance.dredgion;

import ai.OneDmgPerHitAI2;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("surkana")
public class SurkanaAI2 extends OneDmgPerHitAI2
{
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkForSupport(creature);
	}
	
	private void checkForSupport(Creature creature) {
		for (VisibleObject object : getKnownList().getKnownObjects().values()) {
			if (object instanceof Npc && isInRange(object, 20)) {
				((Npc) object).getAi2().onCreatureEvent(AIEventType.CREATURE_AGGRO, creature);
			}
		}
	}
}