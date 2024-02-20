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
package ai.instance.azoturanFortress;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
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

@AIName("Icaronix_The_Betrayer")
public class Icaronix_The_BetrayerAI2 extends AggressiveNpcAI2
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
			    faithfulSubordinate1();
			break;
			case 2:
			    faithfulSubordinate2();
			break;
			case 3:
			    faithfulSubordinate3();
			break;
			case 4:
			    faithfulSubordinate4();
			break;
		}
	}
	
	private void faithfulSubordinate1() {
		///I'll prove you that you are not perfect!
		sendMsg(341532, getObjectId(), false, 0);
		///You fool Daeva are hastening your own death!
		sendMsg(341533, getObjectId(), false, 4000);
		rndSpawn(280937, 1);
	}
	
	private void faithfulSubordinate2() {
		///Just give up. You will be a dead meat anyway.
		sendMsg(341534, getObjectId(), false, 0);
		///Useless Daevas!
		sendMsg(341536, getObjectId(), false, 4000);
		rndSpawn(280938, 1);
	}
	
	private void faithfulSubordinate3() {
		///This is the perfect power!
		sendMsg(341537, getObjectId(), false, 0);
		///I will beat into your head, incomplete Daeva.
		sendMsg(341538, getObjectId(), false, 4000);
		rndSpawn(280939, 1);
	}
	
	private void faithfulSubordinate4() {
		///I'll prove you that you are not perfect!
		sendMsg(341532, getObjectId(), false, 0);
		///Useless Daevas!
		sendMsg(341536, getObjectId(), false, 4000);
		rndSpawn(280940, 1);
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
		killNpc(instance.getNpcs(280937));
		killNpc(instance.getNpcs(280938));
		killNpc(instance.getNpcs(280939));
		killNpc(instance.getNpcs(280940));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
		///I am...a complete subject...how could I be ended by a Daeva?
		sendMsg(341535, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(280937));
		killNpc(instance.getNpcs(280938));
		killNpc(instance.getNpcs(280939));
		killNpc(instance.getNpcs(280940));
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}