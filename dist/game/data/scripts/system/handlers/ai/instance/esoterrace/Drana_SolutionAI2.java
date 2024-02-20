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
package ai.instance.esoterrace;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.poll.*;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.MathUtil;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Drana_Solution")
public class Drana_SolutionAI2 extends NpcAI2
{
	@Override
	public void think() {
	}
	
	@Override
	protected void handleCreatureSee(Creature creature) {
		checkDistance(this, creature);
	}
	
	@Override
	protected void handleCreatureMoved(Creature creature) {
		checkDistance(this, creature);
	}
	
	private void checkDistance(NpcAI2 ai, Creature creature) {
		int owner = getNpcId();
		int skill = 0;
		switch (owner) {
			case 206206:
				skill = 8682;
			break;
		} if (creature instanceof Player) {
			if (getNpcId() == 206206 && MathUtil.isIn3dRangeLimited(getOwner(), creature, 1, 8)) {
				if (!creature.getEffectController().hasAbnormalEffect(skill)) {
					AI2Actions.useSkill(this, skill);
				}
			}
		}
	}
	
	@Override
    public AIAnswer ask(AIQuestion question) {
        switch (question) {
            case CAN_ATTACK_PLAYER:
                return AIAnswers.POSITIVE;
            default:
                return AIAnswers.NEGATIVE;
        }
    }
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
}