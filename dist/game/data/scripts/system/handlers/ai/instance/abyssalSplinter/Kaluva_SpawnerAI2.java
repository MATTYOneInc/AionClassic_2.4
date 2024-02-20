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
package ai.instance.abyssalSplinter;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Kaluva_Spawner")
public class Kaluva_SpawnerAI2 extends NpcAI2
{
	private Future<?> kaluvaSpawnerTask;
	
	@Override
	public void think() {
	}
	
	private void kaluvaSpawner() {
		kaluvaSpawnerTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				switch (Rnd.get(1, 2)) {
					case 1:
					    getOwner().getController().onDelete();
						spawn(281911, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
					break;
					case 2:
					    getOwner().getController().onDelete();
					    spawn(281912, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
					break;
				}
			}
		}, 30000, 120000);
	}
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		kaluvaSpawner();
	}
	
	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		kaluvaSpawnerTask.cancel(true);
		getOwner().getEffectController().removeAllEffects();
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		kaluvaSpawnerTask.cancel(true);
		getOwner().getController().onDelete();
		getOwner().getEffectController().removeAllEffects();
	}
	
	@Override
	public boolean isMoveSupported() {
		return false;
	}
}