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

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.NpcShoutsService;


import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Captain_Murugan")
public class Captain_MuruganAI2 extends AggressiveNpcAI2
{
	private int curentPercent = 100;
	private boolean canThink = true;
	private AtomicBoolean isAggred = new AtomicBoolean(false);
	private List<Integer> percents = new ArrayList<Integer>();
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isAggred.compareAndSet(false, true)) {
			getPosition().getWorldMapInstance().getDoors().get(70).setOpen(false);
		}
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{100, 90, 70, 50, 30, 10});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 100:
					    scheduleSkill(19324, 0); //Arrow Of Grief.
					break;
					case 90:
					case 70:
					case 50:
					case 30:
					case 10:
					    ///Thou shalt come to grief!
						sendMsg(1500193, getObjectId(), false, 0);
						///I'll get rid of the cursed ones first!
						sendMsg(1500194, getObjectId(), false, 4000);
						scheduleSkill(19324, 0); //Arrow Of Grief.
						scheduleSkill(19325, 3000); //Hex Storm.
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
		///My lord Surama... I.. am... sorry.
		sendMsg(1500195, getObjectId(), false, 0);
		///Power is... overflowing...
		sendMsg(1500196, getObjectId(), false, 3000);
		getOwner().getEffectController().removeAllEffects();
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}