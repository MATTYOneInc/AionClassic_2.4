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
package ai.instance.darkPoeta;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Power_Generator")
public class Power_GeneratorAI2 extends AggressiveNpcAI2
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
		switch (Rnd.get(1, 5)) {
			case 1:
			    lightGeneratorCore();
			break;
			case 2:
			    waveGeneratorCore();
			break;
			case 3:
			    torpidityGeneratorCore();
			break;
			case 4:
			    shockwaveGeneratorCore();
			break;
			case 5:
			    confusionGeneratorCore();
			break;
		}
	}
	
	private void lightGeneratorCore() {
		//Precision Ray.
		AI2Actions.useSkill(this, 18123);
		rndSpawn(281088, 3);
	}
	
	private void waveGeneratorCore() {
		//Massive Ray.
		AI2Actions.useSkill(this, 18124);
		rndSpawn(281089, 3);
	}
	
	private void torpidityGeneratorCore() {
		//Ray Web.
		AI2Actions.useSkill(this, 18125);
		rndSpawn(281090, 3);
	}
	
	private void shockwaveGeneratorCore() {
		//Electrocution.
		AI2Actions.useSkill(this, 18126);
		rndSpawn(281091, 3);
	}
	
	private void confusionGeneratorCore() {
		//Confusion Impact.
		AI2Actions.useSkill(this, 18127);
		rndSpawn(281092, 3);
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
		killNpc(instance.getNpcs(281088));
		killNpc(instance.getNpcs(281089));
		killNpc(instance.getNpcs(281090));
		killNpc(instance.getNpcs(281091));
		killNpc(instance.getNpcs(281092));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281088));
		killNpc(instance.getNpcs(281089));
		killNpc(instance.getNpcs(281090));
		killNpc(instance.getNpcs(281091));
		killNpc(instance.getNpcs(281092));
	}
}