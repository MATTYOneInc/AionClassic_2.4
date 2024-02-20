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
package ai.instance.udasTemple;

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
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Devoted_Anurati")
public class Devoted_AnuratiAI2 extends AggressiveNpcAI2
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
		}, 5000, 60000);
	}
	
	private void chooseAttack() {
		switch (Rnd.get(1, 4)) {
			case 1:
			    udasWarrior();
			break;
			case 2:
			    udasAssassin();
			break;
			case 3:
			    udasMage();
			break;
			case 4:
			    udasHealer();
			break;
		}
		///You fools. There is nowhere to run!
		sendMsg(1500031, getObjectId(), false, 0);
		///The enemies are here.
		sendMsg(1500032, getObjectId(), false, 5000);
	}
	
	private void udasWarrior() {
		AI2Actions.useSkill(this, 18745); //Staggering Shock.
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rndSpawn(281501, 1);
			}
		}, 3500);
	}
	private void udasAssassin() {
		AI2Actions.useSkill(this, 18745); //Staggering Shock.
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rndSpawn(281502, 1);
			}
		}, 3500);
	}
	private void udasMage() {
		AI2Actions.useSkill(this, 18745); //Staggering Shock.
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rndSpawn(281503, 1);
			}
		}, 3500);
	}
	private void udasHealer() {
		AI2Actions.useSkill(this, 18745); //Staggering Shock.
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rndSpawn(281504, 1);
			}
		}, 3500);
	}
	
	private void cancelTask() {
		if (skillTask != null && !skillTask.isCancelled()) {
			skillTask.cancel(true);
		}
	}
	
	private void rndSpawn(int npcId, int count) {
		for (int i = 0; i < count; i++) {
			SpawnTemplate template = rndSpawnInRange(npcId, 5);
			SpawnEngine.spawnObject(template, getPosition().getInstanceId());
		}
	}
	
	protected SpawnTemplate rndSpawnInRange(int npcId, float distance) {
		float direction = Rnd.get(0, 199) / 100f;
		float x = (float) (Math.cos(Math.PI * direction) * distance);
        float y = (float) (Math.sin(Math.PI * direction) * distance);
		return SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), npcId, getPosition().getX() + x, getPosition().getY() + y, getPosition().getZ(), getPosition().getHeading());
	}
	
	private void killNpc(List<Npc> npcs) {
		for (Npc npc: npcs) {
			AI2Actions.killSilently(this, npc);
		}
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
		killNpc(instance.getNpcs(281501));
		killNpc(instance.getNpcs(281502));
		killNpc(instance.getNpcs(281503));
		killNpc(instance.getNpcs(281504));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
		///Ugh…
		sendMsg(1500033, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281501));
		killNpc(instance.getNpcs(281502));
		killNpc(instance.getNpcs(281503));
		killNpc(instance.getNpcs(281504));
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}