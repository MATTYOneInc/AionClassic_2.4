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
package ai.instance.padmarashkaCave;

import ai.AggressiveNpcAI2;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.poll.*;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Cave_Collapse")
public class Cave_CollapseAI2 extends AggressiveNpcAI2
{
	@Override
	protected void handleCreatureAggro(Creature creature) {
		AI2Actions.useSkill(this, 20103); //Cave Collapse.
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				getOwner().getController().onDelete();
			}
		}, 20000);
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