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
package ai;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AttackIntention;
import com.aionemu.gameserver.ai2.manager.AttackManager;
import com.aionemu.gameserver.ai2.poll.AIAnswer;
import com.aionemu.gameserver.ai2.poll.AIAnswers;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.model.gameobjects.Homing;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("homing")
public class HomingNpcAI2 extends GeneralNpcAI2
{
	@Override
	public void think() {
	}
	
	@Override
	public AttackIntention chooseAttackIntention() {
		return AttackIntention.SIMPLE_ATTACK;
	}
	
	@Override
	protected void handleAttackComplete() {
		super.handleAttackComplete();
		Homing owner = (Homing) getOwner();
		if (owner.getActiveSkillId() != 0) {
			AttackManager.scheduleNextAttack(this);
		}
	}
	
	@Override
	protected AIAnswer pollInstance(AIQuestion question) {
		switch (question) {
			case SHOULD_DECAY:
				return AIAnswers.NEGATIVE;
			case SHOULD_RESPAWN:
				return AIAnswers.NEGATIVE;
			case SHOULD_REWARD:
				return AIAnswers.NEGATIVE;
			default:
				return null;
		}
	}
}