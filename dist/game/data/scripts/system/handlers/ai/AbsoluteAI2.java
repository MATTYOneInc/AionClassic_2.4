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

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Absolute")
public class AbsoluteAI2 extends GeneralNpcAI2
{
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		despawn();
	}
	
	private void despawn() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				getOwner().getController().onDelete();
			}
		}, 120000);
	}
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
}