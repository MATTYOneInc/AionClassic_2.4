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

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.skillengine.SkillEngine;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Nexus")
public class NexusAI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
	private int curentPercent = 100;
	private List<Integer> percents = new ArrayList<Integer>();
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	@Override
	public void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{100, 50, 25});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 100:
					    scheduleBuff(18604, 0); //Silver Mirror.
						///You shall not pass here!
						sendMsg(1500025, getObjectId(), false, 0);
						///Face death, and despair.
						sendMsg(1500026, getObjectId(), false, 4000);
					break;
					case 50:
					    scheduleSkill(18602, 0); //Blade Of Lunacy.
						scheduleSkill(18603, 1500); //Raging Wind Blade.
						///You shall not pass here!
						sendMsg(1500025, getObjectId(), false, 0);
						///Face death, and despair.
						sendMsg(1500026, getObjectId(), false, 4000);
					break;
					case 25:
					    scheduleBuff(18605, 0); //Manic Rage.
						///You shall not pass here!
						sendMsg(1500025, getObjectId(), false, 0);
						///Face death, and despair.
						sendMsg(1500026, getObjectId(), false, 4000);
					break;
				}
				percents.remove(percent);
				break;
			}
		}
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
	
	@Override
    protected void handleDespawned() {
        super.handleDespawned();
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
		addPercent();
		canThink = true;
		curentPercent = 100;
    }
	
	@Override
    protected void handleDied() {
        super.handleDied();
		percents.clear();
		///Argh…
		sendMsg(1500027, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
    }
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}