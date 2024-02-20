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
package ai.instance.esoterrace;

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
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Dalia_Charlands")
public class Dalia_CharlandsAI2 extends AggressiveNpcAI2
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
					daliaWatcher();
				}
			}
		}, 5000, 120000);
	}
	
	private void daliaWatcher() {
		///Release me...from this curse.
		sendMsg(1500240, getObjectId(), false, 0);
		//Rampage Of Pain.
		AI2Actions.useSkill(this, 19323);
		final Npc daliaWatcher1 = (Npc) spawn(217650, 1185.0000f, 672.0000f, 295.0000f, (byte) 115);
		final Npc daliaWatcher2 = (Npc) spawn(217650, 1201.0000f, 604.0000f, 293.0000f, (byte) 17);
		final Npc daliaWatcher3 = (Npc) spawn(217650, 1219.0000f, 697.0000f, 299.0000f, (byte) 105);
		////////////////////////////////////////////////////////
		daliaWatcher1.getSpawn().setWalkerId("npcpathidf4re_drana_named_path1");
		WalkManager.startWalking((NpcAI2) daliaWatcher1.getAi2());
		////////////////////////////////////////////////////////
		daliaWatcher2.getSpawn().setWalkerId("npcpathidf4re_drana_named_path2");
		WalkManager.startWalking((NpcAI2) daliaWatcher2.getAi2());
		////////////////////////////////////////////////////////
		daliaWatcher3.getSpawn().setWalkerId("npcpathidf4re_drana_named_path3");
		WalkManager.startWalking((NpcAI2) daliaWatcher3.getAi2());
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				SkillEngine.getInstance().getSkill(getOwner(), 19339, 60, getOwner()).useNoAnimationSkill(); //Fleshmending.
			}
		}, 32000);
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
		killNpc(instance.getNpcs(217650));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
		///Thank you for freeing me from my Balaurean chains.
		sendMsg(1500239, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(217650));
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}