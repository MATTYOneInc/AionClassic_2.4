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
package ai.instance.abyssalSplinter;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Yamennes")
public class YamennesAI2 extends AggressiveNpcAI2
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
		Collections.addAll(percents, new Integer[]{100, 75, 50, 25, 15});
	}
	
	private void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 100:
					    ///None shall touch this artefact.
						sendMsg(1500005, getObjectId(), false, 0);
						///You won't touch the artifact, no matter what happens!
						sendMsg(1500012, getObjectId(), false, 5000);
					break;
					case 75:
					    spawnGate1();
					    ///You dare threaten me with attacks such as that!
						sendMsg(1500007, getObjectId(), false, 0);
						///Creatures of Aether, come forth and help me!
						sendMsg(1500008, getObjectId(), false, 5000);
					break;
					case 50:
					    spawnGate2();
					    ///You dare threaten me with attacks such as that!
						sendMsg(1500007, getObjectId(), false, 0);
						///Creatures of Aether, come forth and help me!
						sendMsg(1500008, getObjectId(), false, 5000);
					break;
					case 25:
					    spawnGate3();
					    ///You dare threaten me with attacks such as that!
						sendMsg(1500007, getObjectId(), false, 0);
						///Creatures of Aether, come forth and help me!
						sendMsg(1500008, getObjectId(), false, 5000);
					break;
					case 15:
					    AI2Actions.useSkill(this, 19228); //Delirium.
					break;
				}
				percents.remove(percent);
				break;
			}
		}
	}
	
	private void spawnGate1() {
		///Return to the flow of Aether, Daevas!
		sendMsg(1500009, getObjectId(), false, 10000);
		///Only death awaits those who intrude.
		sendMsg(1500010, getObjectId(), false, 15000);
		///Your pathetic power won't get you to the artifact.
		sendMsg(1500006, getObjectId(), false, 20000);
		//Yamennes opens the Spawn Gate and begins to summon his minions.
		PacketSendUtility.npcSendPacketTime(getOwner(), S_MESSAGE_CODE.STR_MSG_IDAbRe_Core_NmdD_SummonStart, 0);
		final Npc spawnGate1 = getPosition().getWorldMapInstance().getNpc(281906);
		if (spawnGate1 == null) {
			orkanimum1();
            spawn(281906, 366.09772f, 748.8709f, 215.9511f, (byte) 65);
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					SkillEngine.getInstance().getSkill(getOwner(), 19257, 60, getOwner()).useNoAnimationSkill(); //Lapilima's Shield.
				}
			}, 15000);
			//Delete if nobody attack summoned!!!
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					WorldMapInstance instance = getPosition().getWorldMapInstance();
					killNpc(instance.getNpcs(281903));
					killNpc(instance.getNpcs(281904));
					killNpc(instance.getNpcs(281906));
					killNpc(instance.getNpcs(282014));
					killNpc(instance.getNpcs(282015));
				}
			}, 120000); //...2Min
		}
	}
	
	private void spawnGate2() {
		///Return to the flow of Aether, Daevas!
		sendMsg(1500009, getObjectId(), false, 10000);
		///Only death awaits those who intrude.
		sendMsg(1500010, getObjectId(), false, 15000);
		///Your pathetic power won't get you to the artifact.
		sendMsg(1500006, getObjectId(), false, 20000);
		//Yamennes opens the Spawn Gate and begins to summon his minions.
		PacketSendUtility.npcSendPacketTime(getOwner(), S_MESSAGE_CODE.STR_MSG_IDAbRe_Core_NmdD_SummonStart, 0);
		final Npc spawnGate2 = getPosition().getWorldMapInstance().getNpc(282014);
		if (spawnGate2 == null) {
			orkanimum2();
			spawn(282014, 339.74002f, 705.6748f, 215.9530f, (byte) 37);
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					//A summoned Lapilima is healing Yamennes!
					PacketSendUtility.npcSendPacketTime(getOwner(), S_MESSAGE_CODE.STR_MSG_IDAbRe_Core_NmdD_Heal, 0);
					SkillEngine.getInstance().getSkill(getOwner(), 19281, 60, getOwner()).useNoAnimationSkill(); //Lapilima's Healing.
				}
			}, 15000);
			//Delete if nobody attack summoned!!!
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					WorldMapInstance instance = getPosition().getWorldMapInstance();
					killNpc(instance.getNpcs(281903));
					killNpc(instance.getNpcs(281904));
					killNpc(instance.getNpcs(281906));
					killNpc(instance.getNpcs(282014));
					killNpc(instance.getNpcs(282015));
				}
			}, 120000); //...2Min
		}
	}
	
	private void spawnGate3() {
		///Return to the flow of Aether, Daevas!
		sendMsg(1500009, getObjectId(), false, 10000);
		///Only death awaits those who intrude.
		sendMsg(1500010, getObjectId(), false, 15000);
		///Your pathetic power won't get you to the artifact.
		sendMsg(1500006, getObjectId(), false, 20000);
		//Yamennes opens the Spawn Gate and begins to summon his minions.
		PacketSendUtility.npcSendPacketTime(getOwner(), S_MESSAGE_CODE.STR_MSG_IDAbRe_Core_NmdD_SummonStart, 0);
		final Npc spawnGate3 = getPosition().getWorldMapInstance().getNpc(282015);
		if (spawnGate3 == null) {
			orkanimum3();
            spawn(282015, 297.86075f, 728.4751f, 215.8804f, (byte) 1);
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					SkillEngine.getInstance().getSkill(getOwner(), 19290, 60, getTarget()).useNoAnimationSkill(); //Crystal Collision.
				}
			}, 15000);
			//Delete if nobody attack summoned!!!
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					WorldMapInstance instance = getPosition().getWorldMapInstance();
					killNpc(instance.getNpcs(281903));
					killNpc(instance.getNpcs(281904));
					killNpc(instance.getNpcs(281906));
					killNpc(instance.getNpcs(282014));
					killNpc(instance.getNpcs(282015));
				}
			}, 120000); //...2Min
		}
	}
	
	private void orkanimum1() {
		final Npc orkanimum1 = (Npc) spawn(281903, 365.0000f, 748.0000f, 217.0000f, (byte) 70);
		orkanimum1.getSpawn().setWalkerId("Yamennes_Summon_1");
		WalkManager.startWalking((NpcAI2) orkanimum1.getAi2());
	}
	
	private void orkanimum2() {
		final Npc orkanimum2 = (Npc) spawn(281904, 339.0000f, 706.0000f, 217.0000f, (byte) 38);
		orkanimum2.getSpawn().setWalkerId("Yamennes_Summon_2");
		WalkManager.startWalking((NpcAI2) orkanimum2.getAi2());
	}
	
	private void orkanimum3() {
		final Npc orkanimum3 = (Npc) spawn(281903, 297.0000f, 728.0000f, 218.0000f, (byte) 4);
		orkanimum3.getSpawn().setWalkerId("Yamennes_Summon_3");
		WalkManager.startWalking((NpcAI2) orkanimum3.getAi2());
	}
	
	private void killNpc(List<Npc> npcs) {
		for (Npc npc: npcs) {
			AI2Actions.killSilently(this, npc);
		}
	}
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		addPercent();
	}
	
	@Override
    protected void handleDespawned() {
        super.handleDespawned();
		percents.clear();
		getOwner().getEffectController().removeAllEffects();
		//Yamennes' threat level has been reset!
		PacketSendUtility.npcSendPacketTime(getOwner(), S_MESSAGE_CODE.STR_MSG_IDAbRe_Core_NmdD_ResetAggro, 0);
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281903));
		killNpc(instance.getNpcs(281904));
		killNpc(instance.getNpcs(281906));
		killNpc(instance.getNpcs(282014));
		killNpc(instance.getNpcs(282015));
    }
	
	@Override
    protected void handleBackHome() {
        super.handleBackHome();
		addPercent();
		canThink = true;
		curentPercent = 100;
		//Yamennes' threat level has been reset!
		PacketSendUtility.npcSendPacketTime(getOwner(), S_MESSAGE_CODE.STR_MSG_IDAbRe_Core_NmdD_ResetAggro, 0);
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281903));
		killNpc(instance.getNpcs(281904));
		killNpc(instance.getNpcs(281906));
		killNpc(instance.getNpcs(282014));
		killNpc(instance.getNpcs(282015));
    }
	
	@Override
    protected void handleDied() {
        super.handleDied();
		percents.clear();
		///Ah… The power, power of… Aion is…
		sendMsg(1500017, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281903));
		killNpc(instance.getNpcs(281904));
		killNpc(instance.getNpcs(281906));
		killNpc(instance.getNpcs(282014));
		killNpc(instance.getNpcs(282015));
    }
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}