/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.ai2.*;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.controllers.observer.*;
import com.aionemu.gameserver.geoEngine.collision.CollisionIntention;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.model.gameobjects.*;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.skillengine.model.*;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.geo.GeoService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import java.util.concurrent.ScheduledFuture;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FearEffect")
public class FearEffect extends EffectTemplate
{
	@XmlAttribute
	protected final float resistchance = 100;
	
	@Override
	public void applyEffect(Effect effect) {
		effect.getEffected().getEffectController().removeHideEffects();
		effect.addToEffectedController();
	}
	
	@Override
	public void calculate(Effect effect) {
		super.calculate(effect, StatEnum.FEAR_RESISTANCE, null);
	}
	
	@Override
	public void startEffect(final Effect effect) {
		final Creature effector = effect.getEffector();
		final Creature effected = effect.getEffected();
		effected.getController().cancelCurrentSkill();
		effect.setAbnormal(AbnormalState.FEAR.getId());
		effected.getEffectController().setAbnormal(AbnormalState.FEAR.getId());
		if (effected instanceof Npc) {
			((NpcAI2) effected.getAi2()).setStateIfNot(AIState.FEAR);
		}
		ScheduledFuture<?> fearTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new FearTask(effector, effected), 0, 100);
		effect.setPeriodicTask(fearTask, position);
		if (resistchance < 100) {
			ActionObserver observer = new ActionObserver(ObserverType.ATTACKED) {
				@Override
				public void attacked(Creature creature) {
					if (Rnd.get(0, 100) > resistchance) {
						effected.getEffectController().removeEffect(effect.getSkillId());
					}
				}
			};
			effected.getObserveController().addObserver(observer);
			effect.setActionObserver(observer, position);
		}
	}
	
	@Override
	public void endEffect(Effect effect) {
		effect.getEffected().getMoveController().abortMove();
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.FEAR.getId());
		PacketSendUtility.broadcastPacketAndReceive(effect.getEffected(), new S_FORCE_BLINK(effect.getEffected()));
		if (effect.getEffected() instanceof Npc) {
			((NpcAI2) effect.getEffected().getAi2()).onCreatureEvent(AIEventType.ATTACK, effect.getEffector());
		} if (resistchance < 100) {
			ActionObserver observer = effect.getActionObserver(position);
			if (observer != null) {
				effect.getEffected().getObserveController().removeObserver(observer);
			}
		}
	}
	
	class FearTask implements Runnable {
		private Creature effector;
		private Creature effected;
		
		FearTask(Creature effector, Creature effected) {
			this.effector = effector;
			this.effected = effected;
		}
		
		@Override
		public void run() {
			if (effected.getEffectController().isUnderFear()) {
				double radian = Math.toRadians(MathUtil.convertHeadingToDegree(effector.getHeading()));
				float maxDistance = effected.getGameStats().getMovementSpeedFloat();
				float x1 = (float) (Math.cos(radian) * maxDistance);
				float y1 = (float) (Math.sin(radian) * maxDistance);
				byte intentions = (byte) (CollisionIntention.PHYSICAL.getId() | CollisionIntention.DOOR.getId());
				Vector3f closestCollision = GeoService.getInstance().getClosestCollision(effected, effected.getX() + x1, effected.getY() + y1, effected.getZ() - 0.1f, false, intentions);
				if (effected.isFlying()) {
					closestCollision.setZ(effected.getZ() - 0.1f);
				} if (effected instanceof Npc) {
					((Npc) effected).getMoveController().resetMove();
					((Npc) effected).getMoveController().moveToPoint(closestCollision.getX(), closestCollision.getY(), closestCollision.getZ());
				} else {
					effected.getMoveController().setNewDirection(closestCollision.getX(), closestCollision.getY(), closestCollision.getZ(), effector.getHeading());
					effected.getMoveController().startMovingToDestination();
				}
			}
		}
	}
}