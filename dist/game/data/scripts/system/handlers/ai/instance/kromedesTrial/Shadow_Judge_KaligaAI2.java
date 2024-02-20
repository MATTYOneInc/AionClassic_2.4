/*
 * This file is part of aion-lightning <aion-lightning.com>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package ai.instance.kromedesTrial;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.controllers.effect.*;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/****/
/** Author Rinzler (Encom)
/****/

@AIName("Shadow_Judge_Kaliga")
public class Shadow_Judge_KaligaAI2 extends AggressiveNpcAI2
{
	private boolean canThink = true;
	private int curentPercent = 100;
	private List<Integer> percents = new ArrayList<Integer>();
	private AtomicBoolean startedEvent = new AtomicBoolean(false);
	
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
		Collections.addAll(percents, new Integer[]{75, 50});
	}
	
	private synchronized void checkPercentage(int hpPercentage) {
		curentPercent = hpPercentage;
		for (Integer percent: percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 75:
					    templeNagolem1();
						///This is my first judgment!
						sendMsg(1500172, getObjectId(), false, 0);
					break;
					case 50:
					    templeNagolem2();
						///This is my second judgment!
						sendMsg(1500173, getObjectId(), false, 0);
					break;
				}
				percents.remove(percent);
				break;
			}
		}
	}
	
	@Override
    protected void handleCreatureMoved(Creature creature) {
        if (creature instanceof Player) {
            final Player player = (Player) creature;
            if (MathUtil.getDistance(getOwner(), player) <= 25) {
                if (startedEvent.compareAndSet(false, true)) {
                    if (getNpcId() != 217005) {
                        SkillEngine.getInstance().getSkill(getOwner(), 19246, 60, getOwner()).useNoAnimationSkill(); //Judge's Verdict.
                    } else if (getNpcId() != 217006) {
                        SkillEngine.getInstance().getSkill(getOwner(), 19246, 60, getOwner()).useNoAnimationSkill(); //Judge's Verdict.
                    }
                    relicEffect();
                }
            }
        }
    }
	
	private void relicEffect() {
		if (getNpcId() == 217005 || getNpcId() == 217006) {
			///You dare challenge me ? How exciting !
			sendMsg(1500171, getObjectId(), false, 0);
			///Beg for forgiveness, before it's too late!
			sendMsg(1500174, getObjectId(), false, 2000);
			getSpawnTemplate().setWalkerId("npcpath_2up_namede_angry_judge_38_an");
			setStateIfNot(AIState.FIGHT);
			think();
		}
    }
	
	@Override
    protected void handleMoveArrived() {
        int point = getOwner().getMoveController().getCurrentPoint();
        super.handleMoveArrived();
		if (point == 1) {
			//Mana Relic Effect.
			scheduleBuff(19248, 0);
			///How dare you come here!
			sendMsg(1500162, getObjectId(), false, 0);
			spawn(282093, 661.0000f, 766.0000f, 218.0000f, (byte) 0, 87); //Mana Relic.
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					///You have grown powerful, Kromede. But still not enough to contend with me!
					sendMsg(1500164, getObjectId(), false, 0);
					getOwner().getMoveController().moveToPoint(662.4417f, 782.4112f, 216.2621f);
					WalkManager.startWalking(Shadow_Judge_KaligaAI2.this);
					getOwner().setState(1);
					PacketSendUtility.broadcastPacket(getOwner(), new S_ACTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
				}
			}, 5000);
        } else if (point == 2) {
			//Strength Relic Effect.
			scheduleBuff(19247, 0);
			///So, shall we start again, then?
			sendMsg(1500169, getObjectId(), false, 0);
			spawn(282095, 661.0000f, 782.0000f, 218.0000f, (byte) 0, 84); //Strength Relic.
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					///The real battle begins now.
					sendMsg(1500170, getObjectId(), false, 3000);
					getOwner().getMoveController().moveToPoint(659.0813f, 774.3127f, 216.2343f);
					WalkManager.startWalking(Shadow_Judge_KaligaAI2.this);
					getOwner().setState(1);
					PacketSendUtility.broadcastPacket(getOwner(), new S_ACTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
				}
			}, 5000);
        }
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
	
	private void templeNagolem(final Npc npc) {
		for (Player player: getKnownList().getKnownPlayers().values()) {
			npc.setTarget(player);
			((AbstractAI) npc.getAi2()).setStateIfNot(AIState.WALKING);
			npc.setState(1);
			npc.getMoveController().moveToTargetObject();
			PacketSendUtility.broadcastPacket(npc, new S_ACTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
		}
	}
	
	private void templeNagolem1() {
		templeNagolem((Npc)spawn(282124, 653.0000f, 754.0000f, 216.0000f, (byte) 30));
		templeNagolem((Npc)spawn(282124, 653.0000f, 793.0000f, 216.0000f, (byte) 90));
	}
	
	private void templeNagolem2() {
		templeNagolem((Npc)spawn(282124, 633.0000f, 754.0000f, 216.0000f, (byte) 23));
		templeNagolem((Npc)spawn(282124, 633.0000f, 793.0000f, 216.0000f, (byte) 91));
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
	}
	
	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		addPercent();
		canThink = true;
		curentPercent = 100;
		////////////////////////////////////////////
		getOwner().getController().abortCast();
		EmoteManager.emoteStopAttacking(getOwner());
		getOwner().getController().cancelCurrentSkill();
		EffectController ef = getOwner().getEffectController();
		if (ef.hasAbnormalEffect(19247) && ef.hasAbnormalEffect(19248)) {
			ef.removeEffect(19247);
			ef.removeEffect(19248);
		}
		setStateIfNot(AIState.WALKING);
		getOwner().getMoveController().moveToPoint(getOwner().getSpawn().getX(), getOwner().getSpawn().getY(), getOwner().getSpawn().getZ());
		WalkManager.startWalking(Shadow_Judge_KaligaAI2.this);
		getOwner().setState(1);
		PacketSendUtility.broadcastPacket(getOwner(), new S_ACTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
		////////////////////////////////////////////
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(282093));
		killNpc(instance.getNpcs(282095));
		killNpc(instance.getNpcs(282124));
	}
	
	@Override
	protected void handleDied() {
		super.handleDied();
		percents.clear();
		///I need to rest.
		sendMsg(1500165, getObjectId(), false, 0);
		getOwner().getEffectController().removeAllEffects();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		killNpc(instance.getNpcs(282093));
		killNpc(instance.getNpcs(282095));
		killNpc(instance.getNpcs(282124));
	}
	
	private void sendMsg(int msg, int Obj, boolean isShout, int time) {
		NpcShoutsService.getInstance().sendMsg(getPosition().getWorldMapInstance(), msg, Obj, isShout, 0, time);
	}
}