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
package ai.worlds.eltnen;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.skillengine.SkillEngine;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Explosive_Device_Of_Yuditio")
public class Explosive_Device_Of_YuditioAI2 extends AggressiveNpcAI2
{
	@Override
	public void think() {
	}
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		SkillEngine.getInstance().getSkill(getOwner(), 1857, 60, getTarget()).useNoAnimationSkill(); //Aether Explosion.
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				AI2Actions.deleteOwner(Explosive_Device_Of_YuditioAI2.this);
			}
		}, 6000);
	}
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		AI2Actions.deleteOwner(this);
	}
}