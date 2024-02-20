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
package ai.instance.theobomosLab;

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
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Unstable_Triroan")
public class Unstable_TriroanAI2 extends AggressiveNpcAI2
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
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			startSkillTask();
		}
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{100, 75, 50, 25});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 100:
					    scheduleBuff(16699, 0); //Reflect.
						scheduleSkill(16531, 2000); //Smash.
						scheduleSkill(18493, 5000); //Summon Flame.
					break;
					case 75:
					    scheduleSkill(18491, 0); //Summon Lightning.
						scheduleSkill(18492, 4000); //Summon Gust.
					break;
					case 50:
					    scheduleSkill(18489, 0); //Hand Of Weakness.
						scheduleSkill(18490, 4000); //Tree Tendril.
					break;
					case 25:
						scheduleSkill(18487, 0); //Wave Of Storm.
						scheduleSkill(18488, 4000); //Black Tsunami.
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
					triroanElement();
				}
			}
		}, 5000, 60000);
	}
	
	private void triroanElement() {
		switch (Rnd.get(1, 4)) {
			case 1:
			    fireOfTriroan();
			break;
			case 2:
			    waterOfTriroan();
			break;
			case 3:
			    earthOfTriroan();
			break;
			case 4:
			    windOfTriroan();
			break;
		}
	}
	
	private void fireOfTriroan() {
		///Zjvlsms tlq dhcmddptj...!!
		sendMsg(341574, getObjectId(), false, 0);
		final Npc fireOfTriroan = (Npc) spawn(280975, 601.0000f, 488.0000f, 196.0000f, (byte) 60);
		fireOfTriroan.getSpawn().setWalkerId("idlf2a_lab_bidlf2a_summonbabyelemental_50_n_path_00");
		WalkManager.startWalking((NpcAI2) fireOfTriroan.getAi2());
	}
	private void waterOfTriroan() {
		///Qkqdms ajrrh gkqtlek...!!
		sendMsg(341575, getObjectId(), false, 0);
		final Npc waterOfTriroan = (Npc) spawn(280976, 601.0000f, 488.0000f, 196.0000f, (byte) 60);
		waterOfTriroan.getSpawn().setWalkerId("idlf2a_lab_bidlf2a_summonbabyelemental_50_n_path_01");
		WalkManager.startWalking((NpcAI2) waterOfTriroan.getAi2());
	}
	private void earthOfTriroan() {
		///Zkfcnfrms zkfxhlrmsdmf dnlgkdu...!!
		sendMsg(341576, getObjectId(), false, 0);
		final Npc earthOfTriroan = (Npc) spawn(280977, 601.0000f, 488.0000f, 196.0000f, (byte) 60);
		earthOfTriroan.getSpawn().setWalkerId("idlf2a_lab_bidlf2a_summonbabyelemental_50_n_path_02");
		WalkManager.startWalking((NpcAI2) earthOfTriroan.getAi2());
	}
	private void windOfTriroan() {
		///Dkdldhs ehdwjq cjsaksdmf gidgo wjswls...!!
		sendMsg(341577, getObjectId(), false, 0);
		final Npc windOfTriroan = (Npc) spawn(280978, 601.0000f, 488.0000f, 196.0000f, (byte) 60);
		windOfTriroan.getSpawn().setWalkerId("idlf2a_lab_bidlf2a_summonbabyelemental_50_n_path_03");
		WalkManager.startWalking((NpcAI2) windOfTriroan.getAi2());
	}
	
	private void scheduleSkill(final int skillId , int delay) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!isAlreadyDead()) {
					SkillEngine.getInstance().getSkill(getOwner(), skillId, 60, getTarget()).useNoAnimationSkill();
				}
			}
		}, delay);
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
		killNpc(instance.getNpcs(280975));
		killNpc(instance.getNpcs(280976));
		killNpc(instance.getNpcs(280977));
		killNpc(instance.getNpcs(280978));
	}
	
	@Override
    protected void handleDied() {
        super.handleDied();
		cancelTask();
		percents.clear();
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(280975));
		killNpc(instance.getNpcs(280976));
		killNpc(instance.getNpcs(280977));
		killNpc(instance.getNpcs(280978));
    }
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}