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

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.controllers.effect.*;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Warden_Surama")
public class Warden_SuramaAI2 extends AggressiveNpcAI2
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
		Collections.addAll(percents, new Integer[]{90, 70, 50, 30, 10});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 90:
					case 70:
					case 50:
					case 30:
					case 10:
					    airWave();
					break;
				}
				percents.remove(percent);
				break;
		    }
		}
	}
	
	private void airWave() {
		canThink = false;
		getOwner().getController().abortCast();
		EmoteManager.emoteStopAttacking(getOwner());
		getOwner().getController().cancelCurrentSkill();
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				setStateIfNot(AIState.WALKING);
				getOwner().getMoveController().moveToPoint(getOwner().getSpawn().getX(), getOwner().getSpawn().getY(), getOwner().getSpawn().getZ());
				WalkManager.startWalking(Warden_SuramaAI2.this);
				getOwner().setState(1);
				PacketSendUtility.broadcastPacket(getOwner(), new S_ACTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
			}
		}, 2000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				startAirWave();
				///Ha, fly little Daeva... if you can.
				sendMsg(1500202, getObjectId(), false, 0);
				///I'll show you what real power looks like!
				sendMsg(1500203, getObjectId(), false, 5000);
				//Management Director Surama uses Collapsing Earth.
				PacketSendUtility.npcSendPacketTime(getOwner(), S_MESSAGE_CODE.STR_MSG_IDF4Re_Drana_10, 4000);
				SkillEngine.getInstance().getSkill(getOwner(), 19332, 60, getTarget()).useNoAnimationSkill(); //Collapsing Earth.
			}
		}, 6000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				canThink = true;
				WorldMapInstance instance = getPosition().getWorldMapInstance();
				deleteNpcs(instance.getNpcs(282171));
				deleteNpcs(instance.getNpcs(282172));
				deleteNpcs(instance.getNpcs(282173));
				deleteNpcs(instance.getNpcs(282174));
				deleteNpcs(instance.getNpcs(282425));
				deleteNpcs(instance.getNpcs(282426));
				deleteNpcs(instance.getNpcs(282427));
				deleteNpcs(instance.getNpcs(282428));
				Creature creature = getAggroList().getMostHated();
				if (creature == null || creature.getLifeStats().isAlreadyDead() || !getOwner().canSee(creature)) {
					setStateIfNot(AIState.FIGHT);
					think();
				} else {
					getMoveController().abortMove();
					getOwner().setTarget(creature);
					getOwner().getGameStats().renewLastAttackTime();
					getOwner().getGameStats().renewLastAttackedTime();
					getOwner().getGameStats().renewLastChangeTargetTime();
					getOwner().getGameStats().renewLastSkillTime();
					setStateIfNot(AIState.WALKING);
					getOwner().setState(1);
					getOwner().getMoveController().moveToTargetObject();
					PacketSendUtility.broadcastPacket(getOwner(), new S_ACTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
					///I'll... kill you all...
					sendMsg(1500198, getObjectId(), false, 3000);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							SkillEngine.getInstance().getSkill(getOwner(), 19335, 60, getTarget()).useNoAnimationSkill(); //Gravity Blessing.
						}
					}, 3000);
				}
			}
		}, 13000);
	}
	
	private void startAirWave() {
		//The Surkana Steam Jet has generated an updraft.
		PacketSendUtility.npcSendPacketTime(getOwner(), S_MESSAGE_CODE.STR_MSG_IDF4Re_Drana_09, 0);
		spawn(282171, 1316.7438f, 1145.0411f, 51.536953f, (byte) 0, 595);
		spawn(282172, 1342.0642f, 1170.9083f, 51.539276f, (byte) 0, 596);
		spawn(282173, 1316.7826f, 1196.8873f, 51.544514f, (byte) 0, 598);
		spawn(282174, 1290.6938f, 1170.3945f, 51.536175f, (byte) 0, 597);
		spawn(282425, 1304.8906f, 1159.2782f, 51.378143f, (byte) 0, 721);
		spawn(282426, 1328.4050f, 1159.1759f, 51.372219f, (byte) 0, 718);
		spawn(282427, 1328.2821f, 1182.4735f, 51.375172f, (byte) 0, 722);
		spawn(282428, 1304.9152f, 1182.2593f, 51.377087f, (byte) 0, 719);
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
		deleteNpcs(instance.getNpcs(282171));
		deleteNpcs(instance.getNpcs(282172));
		deleteNpcs(instance.getNpcs(282173));
		deleteNpcs(instance.getNpcs(282174));
		deleteNpcs(instance.getNpcs(282425));
		deleteNpcs(instance.getNpcs(282426));
		deleteNpcs(instance.getNpcs(282427));
		deleteNpcs(instance.getNpcs(282428));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		percents.clear();
		///It's a shame... I deceived even Tiamat... and now a mere Daeva...
		sendMsg(1500204, getObjectId(), false, 0);
		///I need... more... Dragel...
		sendMsg(1500199, getObjectId(), false, 5000);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		deleteNpcs(instance.getNpcs(282171));
		deleteNpcs(instance.getNpcs(282172));
		deleteNpcs(instance.getNpcs(282173));
		deleteNpcs(instance.getNpcs(282174));
		deleteNpcs(instance.getNpcs(282425));
		deleteNpcs(instance.getNpcs(282426));
		deleteNpcs(instance.getNpcs(282427));
		deleteNpcs(instance.getNpcs(282428));
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}