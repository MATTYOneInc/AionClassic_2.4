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

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Raging_Kraterr")
public class Raging_KraterrAI2 extends AggressiveNpcAI2
{
	private Future<?> skillTask;
	private AtomicBoolean isHome = new AtomicBoolean(true);
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			startSkillTask();
		}
	}
	
	private void startSkillTask() {
		skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (isAlreadyDead()) {
					cancelTask();
				} else {
					chooseAttack();
				}
			}
		}, 5000, 40000);
	}
	
	private void chooseAttack() {
		switch (Rnd.get(1, 3)) {
			case 1:
			    faithfulServant1();
			break;
			case 2:
			    faithfulServant2();
			break;
			case 3:
			    faithfulServant3();
			break;
		}
		///Minion of darkness, thou shalt rise from thy deep slumber!
		sendMsg(341259, getObjectId(), false, 0);
		///Servant of darkness, it is time for thy revenge!
		sendMsg(341260, getObjectId(), false, 5000);
	}
	
	private void faithfulServant1() {
		//Fire Wave.
		AI2Actions.useSkill(this, 18389);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (getPosition().getWorldMapInstance().getNpc(280333) == null) {
					rndSpawn(280333, 4);
				}
			}
		}, 4000);
	}
	
	private void faithfulServant2() {
		//Fire Wave.
		AI2Actions.useSkill(this, 18389);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (getPosition().getWorldMapInstance().getNpc(280334) == null) {
					rndSpawn(280334, 4);
				}
			}
		}, 4000);
	}
	
	private void faithfulServant3() {
		//Fire Wave.
		AI2Actions.useSkill(this, 18389);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (getPosition().getWorldMapInstance().getNpc(280335) == null) {
					rndSpawn(280335, 4);
				}
			}
		}, 4000);
	}
	
	private void rndSpawn(int npcId, int count) {
		for (int i = 0; i < count; i++) {
			SpawnTemplate template = rndSpawnInRange(npcId, 10);
			SpawnEngine.spawnObject(template, getPosition().getInstanceId());
		}
	}
	
	protected SpawnTemplate rndSpawnInRange(int npcId, float distance) {
		float direction = Rnd.get(0, 199) / 100f;
		float x = (float) (Math.cos(Math.PI * direction) * distance);
        float y = (float) (Math.sin(Math.PI * direction) * distance);
		return SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), npcId, getPosition().getX() + x, getPosition().getY() + y, getPosition().getZ(), getPosition().getHeading());
	}
	
	private void cancelTask() {
		if (skillTask != null && !skillTask.isCancelled()) {
			skillTask.cancel(true);
		}
	}
	
	private void killNpc(List<Npc> npcs) {
		for (Npc npc: npcs) {
			AI2Actions.killSilently(this, npc);
		}
	}
	
	@Override
    protected void handleSpawned() {
        super.handleSpawned();
    }
	
	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		cancelTask();
	}
	
	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		cancelTask();
		isHome.set(true);
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(280333));
		killNpc(instance.getNpcs(280334));
		killNpc(instance.getNpcs(280335));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(280333));
		killNpc(instance.getNpcs(280334));
		killNpc(instance.getNpcs(280335));
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}