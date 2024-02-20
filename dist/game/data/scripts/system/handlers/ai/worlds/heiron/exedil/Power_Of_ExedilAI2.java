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
package ai.worlds.heiron.exedil;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.AI2Actions;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Power_Of_Exedil")
public class Power_Of_ExedilAI2 extends NpcAI2
{
	@Override
	public void think() {
	}
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		despawn();
		AI2Actions.targetCreature(Power_Of_ExedilAI2.this, getPosition().getWorldMapInstance().getNpc(212317)); //Exedil.
		AI2Actions.useSkill(Power_Of_ExedilAI2.this, 17125);
	}
	
    private void despawn() {
  	    ThreadPoolManager.getInstance().schedule(new Runnable() {
  		    @Override
  		    public void run() {
  			    getOwner().getController().onDelete();
  		    }
  	    }, 15000);
    }
	
    @Override
	public boolean isMoveSupported() {
		return false;
	}
}