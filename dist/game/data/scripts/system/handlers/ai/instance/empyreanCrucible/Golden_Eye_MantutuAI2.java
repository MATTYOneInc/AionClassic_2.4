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
package ai.instance.empyreanCrucible;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Golden_Eye_Mantutu2")
public class Golden_Eye_MantutuAI2 extends AggressiveNpcAI2
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
					mantutuBuff();
				}
			}
		}, 5000, 40000);
	}
	
	private void mantutuBuff() {
		switch (Rnd.get(1, 2)) {
			case 1:
				feedSupplyDevice();
			    AI2Actions.useSkill(this, 18180); //Hunger.
			break;
			case 2:
				waterSupplyDevice();
			    AI2Actions.useSkill(this, 18181); //Thirst.
			break;
		}
	}
	
	private void feedSupplyDevice() {
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		deleteNpcs(instance.getNpcs(701387));
		for (Player player: getKnownList().getKnownPlayers().values()) {
			final Npc FeedSupplyDevice = getPosition().getWorldMapInstance().getNpc(701386);
			if (FeedSupplyDevice == null) {
				spawn(701386, 1253.0000f, 789.0000f, 358.0000f, (byte) 119);
			}
		}
	}
	
	private void waterSupplyDevice() {
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		deleteNpcs(instance.getNpcs(701386));
		for (Player player: getKnownList().getKnownPlayers().values()) {
			final Npc WaterSupplyDevice = getPosition().getWorldMapInstance().getNpc(701387);
			if (WaterSupplyDevice == null) {
				spawn(701387, 1260.0000f, 800.0000f, 358.0000f, (byte) 16);
			}
		}
	}
	
	private void cancelTask() {
		if (skillTask != null && !skillTask.isCancelled()) {
			skillTask.cancel(true);
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
		deleteNpcs(instance.getNpcs(701386));
		deleteNpcs(instance.getNpcs(701387));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		deleteNpcs(instance.getNpcs(701386));
		deleteNpcs(instance.getNpcs(701387));
	}
}