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
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Shadowshift")
public class ShadowshiftAI2 extends AggressiveNpcAI2
{
	private Future<?> skillTask;
	private boolean canThink = true;
	private int curentPercent = 100;
	private AtomicBoolean isHome = new AtomicBoolean(true);
	private List<Integer> percents = new ArrayList<Integer>();
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	@Override
	public void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			startSkillTask();
		}
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{75, 50, 25});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 75:	
					case 50:
					case 25:
					    scheduleBuff(18908, 0); //Immortal Reverie.
						///Rebirth... Rebirth... Rebirth...
						sendMsg(1500068, getObjectId(), false, 0);
					break;
				}
				percents.remove(percent);
				break;
			}
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
		switch (Rnd.get(1, 2)) {
			case 1:
			    blackDregs();
			break;
			case 2:
				blackEssence();
			break;
		}
	}
	
	private void blackEssence() {
		///Something… has come…
		sendMsg(1500067, getObjectId(), false, 0);
		for (Player player: getKnownList().getKnownPlayers().values()) {
			final Npc blackEssence = getPosition().getWorldMapInstance().getNpc(281657);
			if (blackEssence == null) {
				spawn(281657, 1387.5917f, 473.1486f, 243.2504f, (byte) 105);
				spawn(281657, 1376.6598f, 462.1534f, 243.2344f, (byte) 105);
				spawn(281657, 1379.4950f, 470.3114f, 243.2118f, (byte) 105);
			}
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				final Npc IDCatacombs1 = getPosition().getWorldMapInstance().getNpc(281657);
				SkillEngine.getInstance().getSkill(IDCatacombs1, 18983, 60, getTarget()).useNoAnimationSkill(); //Ethereal Hand.
			}
		}, 2000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				WorldMapInstance instance = getPosition().getWorldMapInstance();
				deleteNpcs(instance.getNpcs(281657));
			}
		}, 4000);
	}
	
	private void blackDregs() {
		///Something… has come…
		sendMsg(1500067, getObjectId(), false, 0);
		for (Player player: getKnownList().getKnownPlayers().values()) {
			if (isInRange(player, 30)) {
				spawn(281658, player.getX(), player.getY(), player.getZ(), (byte) 0);
			}
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				final Npc IDCatacombs2 = getPosition().getWorldMapInstance().getNpc(281658);
				SkillEngine.getInstance().getSkill(IDCatacombs2, 18984, 60, getTarget()).useNoAnimationSkill(); //Pungent Odor.
			}
		}, 2000);
	}
	
	private void scheduleBuff(final int skillId , int delay) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isAlreadyDead()) {
					SkillEngine.getInstance().getSkill(getOwner(), skillId, 60, getOwner()).useNoAnimationSkill();
				}
			}
		}, delay);
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
		percents.clear();
	}
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		addPercent();
	}
	
	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		cancelTask();
		addPercent();
		canThink = true;
		isHome.set(true);
		curentPercent = 100;
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281657));
		killNpc(instance.getNpcs(281658));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
		percents.clear();
		///Bye... Bye... Bye...
		sendMsg(1500069, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281657));
		killNpc(instance.getNpcs(281658));
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}