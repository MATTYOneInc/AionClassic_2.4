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
package ai.instance.beshmundirTemple;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Thurzon_The_Undying")
public class Thurzon_The_UndyingAI2 extends AggressiveNpcAI2
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
		Collections.addAll(percents, new Integer[]{100, 95, 90, 85, 80, 75, 50, 25});
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
					    ///Thurzon the Undying assumes an attacking posture!
						PacketSendUtility.npcSendPacketTime(getOwner(), S_MESSAGE_CODE.STR_CHAT_IDCatacombs_BonedrakeNmd_55_Ah_Start1, 0);
						scheduleSkill(19043, 0); //Terrorizing Flame.
					    scheduleSkill(19042, 2000); //Inevitable Flame.
					break;
					case 75:
					case 50:
					case 25:
					    immortalWatcher();
						scheduleSkill(18998, 0); //Mental Shock.
						scheduleBuff(19041, 2000); //Mana Tap.
					break;
				}
				percents.remove(percent);
				break;
			}
		}
	}
	
	private void immortalWatcher() {
		///Thurzon the Undying roars!
		PacketSendUtility.npcSendPacketTime(getOwner(), S_MESSAGE_CODE.STR_CHAT_IDCatacombs_BonedrakeNmd_55_Ah_Skill1, 0);
		for (Player player: getKnownList().getKnownPlayers().values()) {
			final Npc immortalWatcher = getPosition().getWorldMapInstance().getNpc(281664);
			if (immortalWatcher == null) {
				spawn(281664, 1394.4099f, 532.856f, 241.24936f, (byte) 60);
			}
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				final Npc watcher1 = getPosition().getWorldMapInstance().getNpc(281664);
				SkillEngine.getInstance().getSkill(watcher1, 19044, 60, getTarget()).useNoAnimationSkill(); //Draw Essence.
			}
		}, 2000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				final Npc watcher2 = getPosition().getWorldMapInstance().getNpc(281664);
				SkillEngine.getInstance().getSkill(watcher2, 19045, 60, getTarget()).useNoAnimationSkill(); //Life Tap.
			}
		}, 7000);
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
		killNpc(instance.getNpcs(281664));
    }
	
	@Override
    protected void handleDied() {
        super.handleDied();
		percents.clear();
		///Thurzon the Undying screams!
		PacketSendUtility.npcSendPacketTime(getOwner(), S_MESSAGE_CODE.STR_CHAT_IDCatacombs_BonedrakeNmd_55_Ah_Die1, 0);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281664));
    }
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}