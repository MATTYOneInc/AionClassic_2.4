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
package ai.instance.telos;

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
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Drana_Amplifier")
public class Drana_AmplifierAI2 extends AggressiveNpcAI2
{
	private Future<?> skillTask;
	private AtomicBoolean isHome = new AtomicBoolean(true);
	private AtomicBoolean isAggred = new AtomicBoolean(false);
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isAggred.compareAndSet(false, true)) {
			getPosition().getWorldMapInstance().getDoors().get(2).setOpen(false);
			//드라나 증폭 장치가 전투 상태에 돌입했습니다.
		    PacketSendUtility.npcSendPacketTime(getOwner(), S_MESSAGE_CODE.STR_MSG_IDLDF1_BionicCPUNamed_55_Ah_EnterAttack, 0);
		} if (isHome.compareAndSet(true, false)) {
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
					chooseAttack();
				}
			}
		}, 5000, 60000);
	}
	
	private void chooseAttack() {
		switch (Rnd.get(1, 2)) {
			case 1:
			    BIDLDF1LizardFeA55Ae();
			break;
			case 2:
			    BIDLDF1BionicCPUClodWorm55An();
			break;
		}
	}
	
	private void BIDLDF1LizardFeA55Ae() {
		//Explosion.
		AI2Actions.useSkill(this, 18583);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				for (Player player: getKnownList().getKnownPlayers().values()) {
					if (isInRange(player, 30)) {
						spawn(282975, player.getX(), player.getY(), player.getZ(), (byte) 0);
					}
				}
			}
		}, 3500);
	}
	
	private void BIDLDF1BionicCPUClodWorm55An() {
		//Explosion.
		AI2Actions.useSkill(this, 18583);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rndSpawn(282974, 6);
			}
		}, 3500);
	}
	
	private void rndSpawn(int npcId, int count) {
		for (int i = 0; i < count; i++) {
			SpawnTemplate template = rndSpawnInRange(npcId, 20);
			SpawnEngine.spawnObject(template, getPosition().getInstanceId());
		}
	}
	
	protected SpawnTemplate rndSpawnInRange(int npcId, float distance) {
		float direction = Rnd.get(0, 199) / 100f;
		float x = (float) (Math.cos(Math.PI * direction) * distance);
        float y = (float) (Math.sin(Math.PI * direction) * distance);
		return SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), npcId, getPosition().getX() + x, getPosition().getY() + y, getPosition().getZ(), getPosition().getHeading());
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
	}
	
	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		cancelTask();
		isHome.set(true);
		getPosition().getWorldMapInstance().getDoors().get(2).setOpen(true);
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(282974));
		killNpc(instance.getNpcs(282975));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		cancelTask();
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(282974));
		killNpc(instance.getNpcs(282975));
	}
}