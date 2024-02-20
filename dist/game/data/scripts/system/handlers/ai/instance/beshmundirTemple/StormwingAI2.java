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
package ai.instance.beshmundirTemple;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Stormwing")
public class StormwingAI2 extends AggressiveNpcAI2
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
					storms();
				}
			}
		}, 5000, 120000);
	}
	
	private void storms() {
		//Threshing Wind.
		AI2Actions.useSkill(this, 18613);
		///Hahaha! You cannot survive the dragon's curse!
		sendMsg(1500088, getObjectId(), false, 0);
		///Look upon me and despair!
		sendMsg(1500087, getObjectId(), false, 4000);
		///The storm will obliterate you!
		sendMsg(1500089, getObjectId(), false, 8000);
		final Npc rootTwister = (Npc) spawn(281794, 561.0000f, 1348.0000f, 223.0000f, (byte) 83);
		final Npc sharpTwister = (Npc) spawn(281796, 536.0000f, 1369.0000f, 223.0000f, (byte) 66);
		rootTwister.getSpawn().setWalkerId("npcpathpath_rudrawindc3");
		WalkManager.startWalking((NpcAI2) rootTwister.getAi2());
		sharpTwister.getSpawn().setWalkerId("npcpathpath_rudrawindc1");
		WalkManager.startWalking((NpcAI2) sharpTwister.getAi2());
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				WorldMapInstance instance = getPosition().getWorldMapInstance();
				deleteNpcs(instance.getNpcs(281794));
				deleteNpcs(instance.getNpcs(281796));
			}
		}, 120000);
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
	
	private void deleteNpcs(List<Npc> npcs) {
		for (Npc npc: npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
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
		killNpc(instance.getNpcs(281794));
		killNpc(instance.getNpcs(281796));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
		///The storm... is eternal....
		sendMsg(1500092, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281794));
		killNpc(instance.getNpcs(281796));
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}