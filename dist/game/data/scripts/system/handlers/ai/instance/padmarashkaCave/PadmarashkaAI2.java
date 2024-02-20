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
package ai.instance.padmarashkaCave;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Padmarashka")
public class PadmarashkaAI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
	private int curentPercent = 100;
	private List<Integer> percents = new ArrayList<Integer>();
	
	@Override
	public void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{99, 95, 85, 75, 65, 55, 50, 30, 20});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 99:
						tiamatProtection();
						///Lowly Daevas such as you would dare?
						sendMsg(1400527, getObjectId(), false, 0);
						///You have leapt into certain death!
						sendMsg(1400528, getObjectId(), false, 5000);
					break;
					case 95:
					case 85:
					case 75:
					case 65:
					case 55:
					    hatchedVeteranDrakan();
					break;
					case 50:
					    padmarashkaEggs();
					break;
					case 30:
					    AI2Actions.useSkill(this, 20100); //Padmarashka's Roar.
					break;
					case 20:
					   rockFall();
					   votaicColumn();
					break;
				}
				percents.remove(percent);
				break;
		    }
		}
	}
	
	private void votaicColumn() {
		if (getPosition().getWorldMapInstance().getNpc(281829) == null) {
			rndSpawn(281829, 10);
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				WorldMapInstance instance = getPosition().getWorldMapInstance();
				deleteNpcs(instance.getNpcs(281829));
			}
		}, 120000);
	}
	
	private void hatchedVeteranDrakan() {
		///I grieve for I couldn't become a dragon!
		sendMsg(1400537, getObjectId(), false, 0);
		///I never cared much for the responsibility of breeding!
		sendMsg(1400538, getObjectId(), false, 5000);
		rndSpawn(282620, 5);
	}
	
	private void padmarashkaEggs() {
		///I must protect the eggs!
		sendMsg(1400541, getObjectId(), false, 0);
		///You will never see the light of day again!
		sendMsg(1400542, getObjectId(), false, 5000);
		AI2Actions.useSkill(this, 19177); //Lay Egg.
		//The huge hidden egg is finally revealed.
		PacketSendUtility.npcSendPacketTime(getOwner(), S_MESSAGE_CODE.STR_MSG_IDDramata_02, 2000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				rndSpawn(282613, 10);
				rndSpawn(282614, 10);
				SkillEngine.getInstance().getSkill(getOwner(), 20099, 60, getTarget()).useNoAnimationSkill(); //Chilling Gaze.
			}
		}, 2000);
	}
	
	private void rockFall() {
		///I laugh at you pathetic Daevas who think you can defeat me!
		sendMsg(1400539, getObjectId(), false, 0);
		///The responsibility of breeding is my will!
		sendMsg(1400540, getObjectId(), false, 5000);
		//The cave starts to crumble from Padmarashka's might.
		PacketSendUtility.npcSendPacketTime(getOwner(), S_MESSAGE_CODE.STR_MSG_IDDramata_03, 0);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				for (Player player: getKnownList().getKnownPlayers().values()) {
					if (isInRange(player, 30)) {
						spawn(282140, player.getX(), player.getY(), player.getZ(), (byte) 0);
					}
				}
			}
		}, 5000);
	}
	
	private void tiamatProtection() {
	    SkillEngine.getInstance().getSkill(getOwner(), 20102, 60, getOwner()).useNoAnimationSkill(); //Tiamat's Protection.
	}
	
	private void rndSpawn(int npcId, int count) {
		for (int i = 0; i < count; i++) {
			SpawnTemplate template = rndSpawnInRange(npcId, 30);
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
		killNpc(instance.getNpcs(282620));
		deleteNpcs(instance.getNpcs(282140));
		deleteNpcs(instance.getNpcs(282613));
		deleteNpcs(instance.getNpcs(282614));
		deleteNpcs(instance.getNpcs(281829));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		percents.clear();
		///It can't end… like this…
		sendMsg(1500110, getObjectId(), false, 0);
		///I should have… Become a… Great dragon…
		sendMsg(1500112, getObjectId(), false, 3000);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(282620));
		deleteNpcs(instance.getNpcs(282140));
		deleteNpcs(instance.getNpcs(282613));
		deleteNpcs(instance.getNpcs(282614));
		deleteNpcs(instance.getNpcs(281829));
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}