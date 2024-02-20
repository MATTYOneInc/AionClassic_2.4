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
package ai.worlds.heiron;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Deputy_Hanuman")
public class Deputy_HanumanAI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
	private int curentPercent = 100;
	private List<Integer> percents = new ArrayList<Integer>();
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
    @Override
	protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
    }
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{85, 65, 50, 25});
	}
	
	private void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 85:
					case 65:
					    //Stun Strike.
						AI2Actions.useSkill(this, 17870);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								faithfulSubordinate1();
							}
						}, 3000);
						///I'm not your opponent!
						sendMsg(341121, getObjectId(), false, 0);
						///A more powerful foe would be better!
						sendMsg(341122, getObjectId(), false, 4000);
						///Should I change the mood?
						sendMsg(340905, getObjectId(), false, 20000);
					break;
					case 50:
					    //Stun Strike.
						AI2Actions.useSkill(this, 17870);
					    ///Chuckle, this is interesting.
						sendMsg(341123, getObjectId(), false, 0);
						///Should I change the mood?
						sendMsg(340905, getObjectId(), false, 20000);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								faithfulSubordinate2();
							}
						}, 3000);
					break;
					case 25:
					    //Stun Strike.
						AI2Actions.useSkill(this, 17870);
					    ///Exterminate them!
						sendMsg(340876, getObjectId(), false, 0);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								faithfulSubordinate1();
								faithfulSubordinate2();
							}
						}, 3000);
					break;
				}
				percents.remove(percent);
				break;
			}
		}
	}
	
	private void faithfulSubordinate1() {
		if (getPosition().getWorldMapInstance().getNpc(280416) == null &&
			getPosition().getWorldMapInstance().getNpc(280417) == null) {
			rndSpawn(280416, 3);
		}
	}
	
	private void faithfulSubordinate2() {
		if (getPosition().getWorldMapInstance().getNpc(280416) == null &&
			getPosition().getWorldMapInstance().getNpc(280417) == null) {
			rndSpawn(280417, 3);
		}
	}
	
	private void rndSpawn(int npcId, int count) {
		for (int i = 0; i < count; i++) {
			SpawnTemplate template = rndSpawnInRange(npcId, 5);
			SpawnEngine.spawnObject(template, getPosition().getInstanceId());
		}
	}
	
	protected SpawnTemplate rndSpawnInRange(int npcId, float distance) {
		float direction = Rnd.get(0, 199) / 100f;
		float x = (float) (Math.cos(Math.PI * direction) * distance);
        float y = (float) (Math.sin(Math.PI * direction) * distance);
		return SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), npcId, getPosition().getX() + x, getPosition().getY() + y, getPosition().getZ(), getPosition().getHeading());
	}
	
	private void killNpc(List<Npc> npcs) {
		for (Npc npc: npcs) {
			AI2Actions.killSilently(this, npc);
		}
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
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(280416));
		killNpc(instance.getNpcs(280417));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		percents.clear();
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(280416));
		killNpc(instance.getNpcs(280417));
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}