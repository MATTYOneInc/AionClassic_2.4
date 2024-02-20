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
package ai.siege;

import ai.AggressiveNpcAI2;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.poll.*;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.*;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("siege_mine")
public class MineAI2 extends AggressiveNpcAI2
{
	@Override
	protected void handleCreatureAggro(Creature creature) {
		tryActivateMine(creature);
	}
	
	private void tryActivateMine(Creature creature) {
		if (!creature.getLifeStats().isAlreadyDead() && MathUtil.isIn3dRangeLimited(getOwner(), creature, 1, 20)) {
			explode(creature);
		}
	}
	
	private void explode(Creature creature) {
		if (setStateIfNot(AIState.FIGHT)) {
			AI2Actions.useSkill(this, 17790);
			AI2Actions.targetCreature(MineAI2.this, creature);
		}
	}
	
	@Override
	protected AIAnswer pollInstance(AIQuestion question) {
		switch (question) {
			case CAN_ATTACK_PLAYER:
                return AIAnswers.POSITIVE;
			default:
				return null;
		}
	}
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
}