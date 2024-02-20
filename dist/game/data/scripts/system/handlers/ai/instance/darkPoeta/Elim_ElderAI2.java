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
package ai.instance.darkPoeta;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Elim_Elder")
public class Elim_ElderAI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
	private AtomicBoolean isAggred = new AtomicBoolean(false);
	private List<Integer> percents = new ArrayList<Integer>();
	
	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isAggred.compareAndSet(false, true)) {
			if (getNpcId() == 214864) { //Noah's Furious Shade.
			    getOwner().getEffectController().setAbnormal(AbnormalState.HIDE.getId());
		        getOwner().unsetVisualState(CreatureVisualState.HIDE1);
		        PacketSendUtility.broadcastPacket(getOwner(), new S_INVISIBLE_LEVEL(getOwner()));
			}
		}
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
	@Override
	public boolean canThink() {
		return canThink;
	}
	
	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{40});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				canThink = false;
				///Do not stand in my path!
				sendMsg(341791, getObjectId(), false, 0);
				///Be gone, you enemy of nature!
				sendMsg(341792, getObjectId(), false, 5000);
				SkillEngine.getInstance().getSkill(getOwner(), 18530, 60, getOwner()).useNoAnimationSkill(); //Become Mist.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						moveAwayElimElder();
					}
				}, 1500);
				percents.remove(percent);
		    }
			break;
		}
	}
	
	private void moveAwayElimElder() {
        if (!isAlreadyDead()) {
			//Noah's Furious Shade.
			if (getNpcId() == 214864) {
				canThink = false;
				EmoteManager.emoteStopAttacking(getOwner());
				getOwner().getMoveController().moveToPoint(601.63153f, 827.33746f, 114.41641f);
				WalkManager.startWalking(this);
				getOwner().setState(1);
				PacketSendUtility.broadcastPacket(getOwner(), new S_ACTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						canThink = true;
						getOwner().getEffectController().removeAllEffects();
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
							ThreadPoolManager.getInstance().schedule(new Runnable() {
								@Override
								public void run() {
									///It is becoming faint, my memories...
									sendMsg(341793, getObjectId(), false, 0);
									SkillEngine.getInstance().getSkill(getOwner(), 18531, 60, getOwner()).useNoAnimationSkill(); //Grudge Explosion.
								}
							}, 3000);
						}
					}
				}, 8000);
			}
			//Spectral Elim Elder.
			if (getNpcId() == 215387) {
				canThink = false;
				EmoteManager.emoteStopAttacking(getOwner());
				getOwner().getMoveController().moveToPoint(606.36816f, 909.3100f, 116.52301f);
				WalkManager.startWalking(this);
				getOwner().setState(1);
				PacketSendUtility.broadcastPacket(getOwner(), new S_ACTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						canThink = true;
						getOwner().getEffectController().removeAllEffects();
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
							ThreadPoolManager.getInstance().schedule(new Runnable() {
								@Override
								public void run() {
									///It is becoming faint, my memories...
									sendMsg(341793, getObjectId(), false, 0);
									SkillEngine.getInstance().getSkill(getOwner(), 18531, 60, getOwner()).useNoAnimationSkill(); //Grudge Explosion.
								}
							}, 3000);
						}
					}
				}, 8000);
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
		getOwner().getEffectController().setAbnormal(AbnormalState.HIDE.getId());
		getOwner().setVisualState(CreatureVisualState.HIDE1);
		PacketSendUtility.broadcastPacket(getOwner(), new S_INVISIBLE_LEVEL(getOwner()));
		addPercent();
	}
	
	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		addPercent();
		canThink = true;
		getOwner().getEffectController().setAbnormal(AbnormalState.HIDE.getId());
		getOwner().setVisualState(CreatureVisualState.HIDE1);
		PacketSendUtility.broadcastPacket(getOwner(), new S_INVISIBLE_LEVEL(getOwner()));
	}
	
	@Override
    protected void handleDied() {
		super.handleDied();
		percents.clear();
		///Defend the forest, we must...
		sendMsg(341794, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
    }
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}