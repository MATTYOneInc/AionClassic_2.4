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

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Freyr")
public class FreyrAI2 extends AggressiveNpcAI2
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
		Collections.addAll(percents, new Integer[]{50});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 50:
						///I never thought you'd make it as far as me.
						sendMsg(1500221, getObjectId(), false, 0);
						///You're every bit as good as they said.
						sendMsg(1500222, getObjectId(), false, 4000);
						///Once the enemy feels fear, they will start to make mistakes.
						sendMsg(1500211, getObjectId(), false, 8000);
						switch (Rnd.get(1, 3)) {
							case 1:
								applySoulSickness((Npc) spawn(282369, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading())); //Traufnir.
							break;
							case 2:
								applySoulSickness((Npc) spawn(282370, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading())); //Sigyn.
							break;
							case 3:
								applySoulSickness((Npc) spawn(282371, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading())); //Sif.
							break;
						}
					break;
				}
				percents.remove(percent);
				break;
			}
		}
	}
	
	private void applySoulSickness(final Npc npc) {
		SkillEngine.getInstance().getSkill(npc, 19594, 60, npc).useNoAnimationSkill();
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
		deleteNpcs(instance.getNpcs(282369));
		deleteNpcs(instance.getNpcs(282370));
		deleteNpcs(instance.getNpcs(282371));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		percents.clear();
		///You have nerves of steel.
		sendMsg(1500212, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		deleteNpcs(instance.getNpcs(282369));
		deleteNpcs(instance.getNpcs(282370));
		deleteNpcs(instance.getNpcs(282371));
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}