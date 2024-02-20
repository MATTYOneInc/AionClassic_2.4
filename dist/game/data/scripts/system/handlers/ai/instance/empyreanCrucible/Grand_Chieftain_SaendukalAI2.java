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

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.skillengine.SkillEngine;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Grand_Chieftain_Saendukal2")
public class Grand_Chieftain_SaendukalAI2 extends AggressiveNpcAI2
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
		Collections.addAll(percents, new Integer[]{70, 50, 25});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 70:
					    scheduleSkill(16861, 0); //Wrath Explosion.
						///I'll take care of the impudent one first!
						sendMsg(340916, getObjectId(), false, 0);
						///Give death.
						sendMsg(341091, getObjectId(), false, 3000);
						///to the miserable soul, kraark!
						sendMsg(341092, getObjectId(), false, 4000);
					break;
					case 50:
					    scheduleSkill(16860, 0); //Pulverizing Assault.
						///A cursed body
						sendMsg(341094, getObjectId(), false, 0);
						///will be rendered frozen.
						sendMsg(341095, getObjectId(), false, 3000);
						///Shatter!
						sendMsg(341096, getObjectId(), false, 4000);
					break;
					case 25:
					    scheduleBuff(16415, 0); //Strong Protection.
						///His majesty the Dragon Lord is with us, kraark!
						sendMsg(341094, getObjectId(), false, 0);
						///Give death.
						sendMsg(341091, getObjectId(), false, 3000);
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
		SkillEngine.getInstance().getSkill(getOwner(), 16873, 60, getOwner()).useNoAnimationSkill(); //Bellicosity.
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
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}