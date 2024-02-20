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
package ai.instance.tallocsHollow;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.SkillEngine;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Kinquid")
public class KinquidAI2 extends AggressiveNpcAI2
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
		getPosition().getWorldMapInstance().getDoors().get(48).setOpen(false);
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{95, 85, 75, 65, 55, 45, 35, 25, 15, 5});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 95:
					case 85:
					case 75:
					case 65:
					case 55:
					    scheduleSkill(18818, 0); //Kinquid's Thrust.
						scheduleSkill(18820, 4000); //Kinquid's Bite.
						scheduleSkill(18822, 8000); //Kinquid's Toxic Fang.
					break;
					case 45:
					case 35:
					case 25:
					case 15:
					case 5:
					    scheduleSkill(18819, 4000); //Kinquid's Blast.
						scheduleBuff(18817, 8000); //Thornbush Armour.
					    switch (Rnd.get(1, 2)) {
						    case 1:
							    scheduleBuff(19233, 0); //Physical Barrier.
							break;
							case 2:
							    scheduleBuff(19234, 0); //Magical Barrier.
							break;
						}
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
		getOwner().getEffectController().removeAllEffects();
    }
}