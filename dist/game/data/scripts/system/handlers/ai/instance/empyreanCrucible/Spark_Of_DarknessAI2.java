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
package ai.instance.empyreanCrucible;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.poll.*;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Spark_Of_Darkness")
public class Spark_Of_DarknessAI2 extends NpcAI2
{
	@Override
	public void think() {
	}
	
	@Override
	protected void handleCreatureSee(Creature creature) {
		checkDistance(creature);
	}
	
	@Override
	protected void handleCreatureMoved(Creature creature) {
		checkDistance(creature);
	}
	
	private void checkDistance(Creature creature) {
		int owner = getNpcId();
		int skill = 0;
		switch (owner) {
			case 282373:
				skill = 19554;
			break;
		} if (creature instanceof Player) {
			if (getNpcId() == 282373 && MathUtil.isIn3dRangeLimited(getOwner(), creature, 1, 15)) {
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