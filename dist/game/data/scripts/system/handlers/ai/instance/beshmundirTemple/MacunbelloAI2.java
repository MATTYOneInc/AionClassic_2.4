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
import com.aionemu.gameserver.ai2.NpcAI2;
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
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("macunbello")
public class MacunbelloAI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
	private AtomicBoolean isAggred = new AtomicBoolean(false);
	private List<Integer> percents = new ArrayList<Integer>();
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isAggred.compareAndSet(false, true)) {
			getPosition().getWorldMapInstance().getDoors().get(467).setOpen(false);
		}
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{90, 70, 50, 30, 10});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				percents.remove(percent);
				canThink = false;
				getOwner().getController().abortCast();
				EmoteManager.emoteStopAttacking(getOwner());
				getOwner().getController().cancelCurrentSkill();
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				  	@Override
				  	public void run() {
						macunbelloRightHand();
						///Come forth, my faithful servants!
						sendMsg(1500061, getObjectId(), false, 0);
						///Hurry and devour these foolish Daevas!
						sendMsg(1500062, getObjectId(), false, 4000);
				  		setStateIfNot(AIState.WALKING);
						SkillEngine.getInstance().getSkill(getOwner(), 19049, 60, getOwner()).useNoAnimationSkill(); //Devour Soul.
				  		getOwner().getMoveController().moveToPoint(getOwner().getSpawn().getX(), getOwner().getSpawn().getY(), getOwner().getSpawn().getZ());
				  		WalkManager.startWalking(MacunbelloAI2.this);
						getOwner().setState(1);
						PacketSendUtility.broadcastPacket(getOwner(), new S_ACTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
				  	}
			    }, 4000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				  	@Override
				  	public void run() {
				  		canThink = true;
				  		EffectController ef = getOwner().getEffectController();
						if (ef.hasAbnormalEffect(19049)) {
							ef.removeEffect(19049);
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
				}, 34000);
		    }
			break;
		}
	}
	
	private void macunbelloRightHand() {
		final Npc rightHand1 = (Npc) spawn(281698, 1004.000f, 159.0000f, 241.0000f, (byte) 91);
		final Npc rightHand2 = (Npc) spawn(281698, 952.0000f, 109.0000f, 242.0000f, (byte) 30);
		final Npc rightHand3 = (Npc) spawn(281698, 955.0000f, 160.0000f, 241.0000f, (byte) 95);
		final Npc rightHand4 = (Npc) spawn(281698, 1006.000f, 110.0000f, 242.0000f, (byte) 51);
		rightHand1.getSpawn().setWalkerId("Right_Hand_1");
		WalkManager.startWalking((NpcAI2) rightHand1.getAi2());
		rightHand2.getSpawn().setWalkerId("Right_Hand_2");
		WalkManager.startWalking((NpcAI2) rightHand2.getAi2());
		rightHand3.getSpawn().setWalkerId("Right_Hand_3");
		WalkManager.startWalking((NpcAI2) rightHand3.getAi2());
		rightHand4.getSpawn().setWalkerId("Right_Hand_4");
		WalkManager.startWalking((NpcAI2) rightHand4.getAi2());
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
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281698));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		percents.clear();
		///Waagh!
		sendMsg(1500063, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(281698));
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}