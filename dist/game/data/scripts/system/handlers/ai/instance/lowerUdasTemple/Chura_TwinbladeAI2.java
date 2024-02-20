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
package ai.instance.lowerUdasTemple;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.NpcShoutsService;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Churatwin_Blade")
public class Chura_TwinbladeAI2 extends AggressiveNpcAI2
{
	private int curentPercent = 100;
	private boolean canThink = true;
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
		Collections.addAll(percents, new Integer[]{50, 10});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 50:
					case 10:
						///How dare you come here!
						sendMsg(1500028, getObjectId(), false, 0);
						///Off to the darkness, all of you!
						sendMsg(1500029, getObjectId(), false, 4000);
						scheduleSkill(18624, 0); //Signet Blast.
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
					SkillEngine.getInstance().getSkill(getOwner(), 18624, 60, getTarget()).useNoAnimationSkill();
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
		///Argh...
		sendMsg(1500030, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
    }
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}