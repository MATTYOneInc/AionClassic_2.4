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
package ai.instance.crucibleChallenge;

import ai.AggressiveNpcAI2;

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

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Hamerun_Leechsoul")
public class Hamerun_LeechsoulAI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
	private List<Integer> percents = new ArrayList<Integer>();
	private AtomicBoolean isAggred = new AtomicBoolean(false);
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isAggred.compareAndSet(false, true)) {
			///Aren't you a fearless one, to charge into Haramel like this!
			sendMsg(1500099, getObjectId(), false, 0);
		}
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{50});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				percents.remove(percent);
				canThink = false;
				///I won't hand over my precious treasures so easily!
				sendMsg(1500100, getObjectId(), false, 0);
				///I will turn you into another one of my minions!
				sendMsg(1500101, getObjectId(), false, 3000);
				getOwner().getController().abortCast();
				EmoteManager.emoteStopAttacking(getOwner());
				getOwner().getController().cancelCurrentSkill();
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				  	@Override
				  	public void run() {
				  		setStateIfNot(AIState.WALKING);
						SkillEngine.getInstance().getSkill(getOwner(), 19210, 60, getOwner()).useNoAnimationSkill(); //Hamerun's Hypnosis.
				  		getOwner().getMoveController().moveToPoint(getOwner().getSpawn().getX(), getOwner().getSpawn().getY(), getOwner().getSpawn().getZ());
				  		WalkManager.startWalking(Hamerun_LeechsoulAI2.this);
						getOwner().setState(1);
						PacketSendUtility.broadcastPacket(getOwner(), new S_ACTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
				  	}
			    }, 4000);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
				  	@Override
				  	public void run() {
				  		canThink = true;
						EffectController ef = getOwner().getEffectController();
						if (ef.hasAbnormalEffect(19210)) {
							ef.removeEffect(19210);
						}
				  		Creature creature = getAggroList().getMostHated();
						getOwner().getEffectController().removeAllEffects();
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
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		percents.clear();
		///Beaten again... But this still isn't over....
		sendMsg(342440, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}