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
package ai.instance.steelRake;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AI2Actions;
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

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Brass_Eye_Grogget")
public class Brass_Eye_GroggetAI2 extends AggressiveNpcAI2
{
	private Future<?> checkTask;
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
		Collections.addAll(percents, new Integer[]{100, 95, 80, 70, 55, 45, 30, 15});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 100:
					    ///You dare to oppose me!
						sendMsg(341895, getObjectId(), false, 0);
						///I will show you the skills of the Shulacks.
						sendMsg(341902, getObjectId(), false, 4000);
					break;
					case 95:
						scheduleSkill(18203, 0); //Enhance Amplifier.
						scheduleSkill(18192, 5000); //Activate Amplifier.
						///Feel what it's like to be defeated by the power of Stigma!
		                sendMsg(341906, getObjectId(), false, 0);
						///An eye for an eye, a tooth for a tooth, and a Stigma for a Stigma!
						sendMsg(341972, getObjectId(), false, 5000);
					    spawn(281191, 397.45663f, 504.2935f, 1072.3727f, (byte) 59);
					break;
					case 80:
					    captainPride();
						///Anyway, where the hell are these guys when the Captain is in trouble!
		                sendMsg(341970, getObjectId(), false, 0);
						///You will not touch even a hair!
		                sendMsg(341898, getObjectId(), false, 5000);
					break;
					case 70:
						///Shulacks are more powerful than Daevas!
						sendMsg(341903, getObjectId(), false, 0);
						///This is the power of your ancestors!
						sendMsg(341907, getObjectId(), false, 5000);
						scheduleSkill(18203, 0); //Enhance Amplifier.
						scheduleSkill(18192, 5000); //Activate Amplifier.
						spawn(281192, 397.04257f, 516.25775f, 1072.3905f, (byte) 43);
					break;
					case 55:
					    captainPride();
						///Guards, guards!
		                sendMsg(341899, getObjectId(), false, 0);
						///Remove these annoying bugs from here!
						sendMsg(341900, getObjectId(), false, 4000);
					break;
					case 45:
					    scheduleSkill(18203, 0); //Enhance Amplifier.
						scheduleSkill(18192, 5000); //Activate Amplifier.
						///Fire Steel Rake!
		                sendMsg(341910, getObjectId(), false, 0);
						///Behold the might of the Steel Beard Pirates!
		                sendMsg(341904, getObjectId(), false, 5000);
						spawn(281193, 409.72568f, 504.62695f, 1072.4496f, (byte) 63);
					break;
					case 30:
					    AI2Actions.useSkill(this, 18200); //Curse Of Roots.
						///Turn into a tree! I really love this skill!
						sendMsg(341909, getObjectId(), false, 0);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								captainPride();
							}
						}, 1500);
					break;
					case 15:
					    AI2Actions.useSkill(this, 18200); //Curse Of Roots.
						///I didn't think I'd have to resort to this! Brace yourself!
		                sendMsg(341905, getObjectId(), false, 0);
						///You did try hard, but it's still not enough against me!
		                sendMsg(341968, getObjectId(), false, 15000);
						spawn(281194, 409.08514f, 516.5806f, 1072.4414f, (byte) 56);
					break;
				}
				percents.remove(percent);
				break;
		    }
		}
	}
	
	private void captainPride() {
		canThink = false;
		getOwner().getController().abortCast();
		EmoteManager.emoteStopAttacking(getOwner());
		getOwner().getController().cancelCurrentSkill();
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				setStateIfNot(AIState.WALKING);
				SkillEngine.getInstance().getSkill(getOwner(), 18191, 60, getOwner()).useNoAnimationSkill(); //Captain's Pride.
				getOwner().getMoveController().moveToPoint(getOwner().getSpawn().getX(), getOwner().getSpawn().getY(), getOwner().getSpawn().getZ());
				WalkManager.startWalking(Brass_Eye_GroggetAI2.this);
				getOwner().setState(1);
				PacketSendUtility.broadcastPacket(getOwner(), new S_ACTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
			}
		}, 2000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				steelRakeHelper();
			}
		}, 6000);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				checkSteelRakeHelper();
			}
		}, 10000);
	}
	
	private void checkSteelRakeHelper() {
		checkTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				final Npc steelRakeDefender = getPosition().getWorldMapInstance().getNpc(281181);
				final Npc steelRakeSeawolf = getPosition().getWorldMapInstance().getNpc(281182);
				final Npc steelRakeWitch = getPosition().getWorldMapInstance().getNpc(281183);
				final Npc steelRakeSurgeon = getPosition().getWorldMapInstance().getNpc(281184);
				if (steelRakeDefender == null && steelRakeSurgeon == null && steelRakeSeawolf == null && steelRakeWitch == null) {
					canThink = true;
					checkTask.cancel(true);
					///Hmph hmph, I feel exhausted from all the shouting. Anyway, here we begin!
		            sendMsg(341974, getObjectId(), false, 0);
					///Now that all my men are here, let's get serious with this fight!
		            sendMsg(341973, getObjectId(), false, 6000);
					EffectController ef = getOwner().getEffectController();
					if (ef.hasAbnormalEffect(18191)) { //Captain's Pride.
						ef.removeEffect(18191);
					}
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
					}
				}
			}
		}, 1 * 500, 1 * 500);
	}
	
	private void steelRakeHelper() {
		spawn(281181, 425.0000f, 496.0000f, 1075.0000f, (byte) 49);
		spawn(281182, 425.0000f, 524.0000f, 1075.0000f, (byte) 68);
		spawn(281183, 390.0000f, 525.0000f, 1072.0000f, (byte) 104);
		spawn(281184, 390.0000f, 493.0000f, 1072.0000f, (byte) 19);
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
		killNpc(instance.getNpcs(281181));
		killNpc(instance.getNpcs(281182));
		killNpc(instance.getNpcs(281183));
		killNpc(instance.getNpcs(281184));
		killNpc(instance.getNpcs(281191));
		killNpc(instance.getNpcs(281192));
		killNpc(instance.getNpcs(281193));
		killNpc(instance.getNpcs(281194));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		percents.clear();
		///Impossible... How could a greenhorn like you defeat me...
		sendMsg(341894, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281181));
		killNpc(instance.getNpcs(281182));
		killNpc(instance.getNpcs(281183));
		killNpc(instance.getNpcs(281184));
		killNpc(instance.getNpcs(281191));
		killNpc(instance.getNpcs(281192));
		killNpc(instance.getNpcs(281193));
		killNpc(instance.getNpcs(281194));
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}