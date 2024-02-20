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

import com.aionemu.commons.utils.Rnd;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.controllers.effect.*;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

import java.util.*;
import java.util.concurrent.Future;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Pazuzu")
public class PazuzuAI2 extends AggressiveNpcAI2
{
	private Future<?> checkTask;
	private boolean canThink = true;
	private int curentPercent = 100;
	private List<Integer> percents = new ArrayList<Integer>();
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{90, 75, 50, 25});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 90:
					    ///Curses to those who disturb the Current of Life.
						sendMsg(1500001, getObjectId(), false, 0);
						///Oh, Breath of Life, return to the embrace of Pazuzu!
						sendMsg(1500002, getObjectId(), false, 4000);
						getOwner().getController().abortCast();
						EmoteManager.emoteStopAttacking(getOwner());
				        getOwner().getController().cancelCurrentSkill();
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								setStateIfNot(AIState.WALKING);
								SkillEngine.getInstance().getSkill(getOwner(), 19145, 60, getOwner()).useNoAnimationSkill(); //Reflective Barrier.
								getOwner().getMoveController().moveToPoint(getOwner().getSpawn().getX(), getOwner().getSpawn().getY(), getOwner().getSpawn().getZ());
								WalkManager.startWalking(PazuzuAI2.this);
								getOwner().setState(1);
								PacketSendUtility.broadcastPacket(getOwner(), new S_ACTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
							}
						}, 4000);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								waterworm2();
							}
						}, 5000);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								checkLuminousWaterworm();
								getOwner().getEffectController().setAbnormal(AbnormalState.SLEEP.getId());
							}
						}, 6000);
					break;
					case 75:
						///Curses to those who disturb the Current of Life.
						sendMsg(1500001, getObjectId(), false, 0);
						///Oh, Breath of Life, return to the embrace of Pazuzu!
						sendMsg(1500002, getObjectId(), false, 4000);
						getOwner().getController().abortCast();
						EmoteManager.emoteStopAttacking(getOwner());
				        getOwner().getController().cancelCurrentSkill();
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								setStateIfNot(AIState.WALKING);
								SkillEngine.getInstance().getSkill(getOwner(), 19145, 60, getOwner()).useNoAnimationSkill(); //Reflective Barrier.
								getOwner().getMoveController().moveToPoint(getOwner().getSpawn().getX(), getOwner().getSpawn().getY(), getOwner().getSpawn().getZ());
								WalkManager.startWalking(PazuzuAI2.this);
								getOwner().setState(1);
								PacketSendUtility.broadcastPacket(getOwner(), new S_ACTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
							}
						}, 4000);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								waterworm3();
							}
						}, 5000);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								checkLuminousWaterworm();
								getOwner().getEffectController().setAbnormal(AbnormalState.SLEEP.getId());
							}
						}, 6000);
					break;
					case 50:
						///Curses to those who disturb the Current of Life.
						sendMsg(1500001, getObjectId(), false, 0);
						///Oh, Breath of Life, return to the embrace of Pazuzu!
						sendMsg(1500002, getObjectId(), false, 4000);
						getOwner().getController().abortCast();
						EmoteManager.emoteStopAttacking(getOwner());
				        getOwner().getController().cancelCurrentSkill();
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								setStateIfNot(AIState.WALKING);
								SkillEngine.getInstance().getSkill(getOwner(), 19145, 60, getOwner()).useNoAnimationSkill(); //Reflective Barrier.
								getOwner().getMoveController().moveToPoint(getOwner().getSpawn().getX(), getOwner().getSpawn().getY(), getOwner().getSpawn().getZ());
								WalkManager.startWalking(PazuzuAI2.this);
								getOwner().setState(1);
								PacketSendUtility.broadcastPacket(getOwner(), new S_ACTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
							}
						}, 4000);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								waterworm4();
							}
						}, 5000);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								checkLuminousWaterworm();
								getOwner().getEffectController().setAbnormal(AbnormalState.SLEEP.getId());
							}
						}, 6000);
					break;
					case 25:
						///Curses to those who disturb the Current of Life.
						sendMsg(1500001, getObjectId(), false, 0);
						///Oh, Breath of Life, return to the embrace of Pazuzu!
						sendMsg(1500002, getObjectId(), false, 4000);
						getOwner().getController().abortCast();
						EmoteManager.emoteStopAttacking(getOwner());
				        getOwner().getController().cancelCurrentSkill();
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								setStateIfNot(AIState.WALKING);
								SkillEngine.getInstance().getSkill(getOwner(), 19145, 60, getOwner()).useNoAnimationSkill(); //Reflective Barrier.
								getOwner().getMoveController().moveToPoint(getOwner().getSpawn().getX(), getOwner().getSpawn().getY(), getOwner().getSpawn().getZ());
								WalkManager.startWalking(PazuzuAI2.this);
								getOwner().setState(1);
								PacketSendUtility.broadcastPacket(getOwner(), new S_ACTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
							}
						}, 4000);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								waterworm5();
							}
						}, 5000);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								checkLuminousWaterworm();
								getOwner().getEffectController().setAbnormal(AbnormalState.SLEEP.getId());
							}
						}, 6000);
					break;
				}
				percents.remove(percent);
				break;
			}
		}
	}
	
	private void checkLuminousWaterworm() {
		checkTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				final Npc luminousWaterworm = getPosition().getWorldMapInstance().getNpc(281909);
				if (luminousWaterworm == null) {
					canThink = true;
					checkTask.cancel(true);
					EffectController ef = getOwner().getEffectController();
					if (ef.hasAbnormalEffect(19145)) { //Reflective Barrier.
						ef.removeEffect(19145);
					}
					Creature creature = getAggroList().getMostHated();
					getOwner().getEffectController().unsetAbnormal(AbnormalState.SLEEP.getId());
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
	
	private void waterworm2() {
		final Npc waterworm = getPosition().getWorldMapInstance().getNpc(281909);
		if (waterworm == null) {
			spawn(281909, 685.0000f, 342.0000f, 465.0000f, (byte) 72);
			spawn(281909, 665.0000f, 350.0000f, 466.0000f, (byte) 89);
		}
	}
	private void waterworm3() {
		final Npc waterworm = getPosition().getWorldMapInstance().getNpc(281909);
		if (waterworm == null) {
			spawn(281909, 685.0000f, 342.0000f, 465.0000f, (byte) 72);
			spawn(281909, 665.0000f, 350.0000f, 466.0000f, (byte) 89);
			spawn(281909, 651.0000f, 346.0000f, 465.0000f, (byte) 109);
		}
	}
	private void waterworm4() {
		final Npc waterworm = getPosition().getWorldMapInstance().getNpc(281909);
		if (waterworm == null) {
			spawn(281909, 685.0000f, 342.0000f, 465.0000f, (byte) 72);
			spawn(281909, 665.0000f, 350.0000f, 466.0000f, (byte) 89);
			spawn(281909, 651.0000f, 346.0000f, 465.0000f, (byte) 109);
			spawn(281909, 650.0000f, 326.0000f, 465.0000f, (byte) 14);
		}
	}
	private void waterworm5() {
		final Npc waterworm = getPosition().getWorldMapInstance().getNpc(281909);
		if (waterworm == null) {
			spawn(281909, 685.0000f, 342.0000f, 465.0000f, (byte) 72);
			spawn(281909, 665.0000f, 350.0000f, 466.0000f, (byte) 89);
			spawn(281909, 651.0000f, 346.0000f, 465.0000f, (byte) 109);
			spawn(281909, 650.0000f, 326.0000f, 465.0000f, (byte) 14);
			spawn(281909, 667.0000f, 313.0000f, 465.0000f, (byte) 21);
		}
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
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281909));
	}
	
	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		addPercent();
		canThink = true;
		curentPercent = 100;
		checkTask.cancel(true);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281909));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		percents.clear();
		checkTask.cancel(true);
		///Finally... I can return to him...
		sendMsg(1500003, getObjectId(), false, 0);
		///Pazuzu has finally... found a way to return...
		sendMsg(1500004, getObjectId(), false, 3000);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281909));
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}