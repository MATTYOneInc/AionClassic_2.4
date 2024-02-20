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

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.actions.CreatureActions;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;
import com.aionemu.gameserver.world.World;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Debilkarim_The_Maker")
public class Debilkarim_The_MakerAI2 extends AggressiveNpcAI2
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
		Collections.addAll(percents, new Integer[]{100, 95, 90, 85, 80, 75, 50, 25, 10});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
				    case 100:
					case 95:
					case 90:
					case 85:
					case 80:
					    scheduleSkill(18634, 0); //Stone Curse.
					break;
					case 75:
					    scheduleSkill(18633, 0); //Rockfall.
					    scheduleSkill(18635, 5000); //Wave Of Fear.
					break;
					case 50:
					    protectionOfAion();
						scheduleSkill(18633, 0); //Rockfall.
					    scheduleSkill(18635, 5000); //Wave Of Fear.
					    scheduleBuff(18636, 8500); //Invoke Energy.
					break;
					case 25:
					    scheduleSkill(18633, 0); //Rockfall.
					    scheduleSkill(18635, 5000); //Wave Of Fear.
					break;
					case 10:
					    rndSpawn(281422, 6); //Pyre Soul.
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
					SkillEngine.getInstance().getSkill(getOwner(), skillId, 1, getTarget()).useNoAnimationSkill();
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
	
	private void protectionOfAion() {
		///Who dares seek out the Petralith?
		sendMsg(1500038, getObjectId(), false, 0);
		///I'll kill anyone who disturbs my work!
		sendMsg(1500036, getObjectId(), false, 5000);
		///You won't get past me!
		sendMsg(1500040, getObjectId(), false, 10000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rndSpawn(281420, 6);
			}
		}, 5500);
	}
	
    private void rndSpawn(int npcId, int count) {
		for (int i = 0; i < count; i++) {
			SpawnTemplate template = rndSpawnInRange(npcId, 10);
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
		killNpc(instance.getNpcs(281420));
		killNpc(instance.getNpcs(281422));
    }
	
	@Override
    protected void handleDied() {
        super.handleDied();
		percents.clear();
		///Great Aion! Please forgive your poor servant...who has failed to fulfill his duties...
		sendMsg(1500039, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281420));
		killNpc(instance.getNpcs(281422));
    }
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}