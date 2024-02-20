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
package ai.instance.arena;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.poll.*;

import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Crater")
public class CraterAI2 extends NpcAI2
{
	private Future<?> craterExplosionTask;
	
	@Override
	public void think() {
	}
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		craterExplosion();
	}
	
	private void craterExplosion() {
		craterExplosionTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				AI2Actions.useSkill(CraterAI2.this, 20069); //Crater Explosion.
			}
		}, 3000, 10000);
	}
	
	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		craterExplosionTask.cancel(true);
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