/*
 * This file is part of aion-unique <aion-unique.com>.
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
package com.aionemu.gameserver.skillengine.properties;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.Trap;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PositionUtil;
import org.apache.commons.lang.math.FloatRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TargetRangeProperty
{
	private static final Logger log = LoggerFactory.getLogger(TargetRangeProperty.class);
	
	public static final boolean set(final Skill skill, Properties properties) {
		TargetRangeAttribute value = properties.getTargetType();
		int distance = properties.getTargetDistance();
		int maxcount = properties.getTargetMaxCount();
		final List<Creature> effectedList = skill.getEffectedList();
		skill.setTargetRangeAttribute(value);
		switch (value) {
			case ONLYONE:
			break;
			case AREA:
				final Creature firstTarget = skill.getFirstTarget();
				if (firstTarget == null) {
					log.warn("CHECKPOINT: first target is null for skillid " + skill.getSkillTemplate().getSkillId());
					return false;
				} for (VisibleObject nextCreature : firstTarget.getKnownList().getKnownObjects().values()) {
					if (((nextCreature instanceof Creature)) && (firstTarget != nextCreature) && (((Creature) nextCreature).getLifeStats() != null)
						&& (!((Creature) nextCreature).getLifeStats().isAlreadyDead()) && ((!(skill.getEffector() instanceof Trap)) || (((Trap) skill.getEffector()).getCreator() != nextCreature))
						&& ((!(nextCreature instanceof Player)) || (!((Player) nextCreature).isProtectionActive()))) {
						if (skill.isPointSkill()) {
							if (MathUtil.isIn3dRange(skill.getX(), skill.getY(), skill.getZ(), nextCreature.getX(), nextCreature.getY(), nextCreature.getZ(), distance + 5)) {
								skill.getEffectedList().add((Creature) nextCreature);
							}
						} else if (properties.getEffectiveWidth() > 0) {
							if (MathUtil.isInsideAttackCylinder(firstTarget, nextCreature, distance, properties.getEffectiveWidth(), !properties.isBackDirection())) {
								if (skill.shouldAffectTarget(nextCreature)) {
									skill.getEffectedList().add((Creature) nextCreature);
								}
							}
						} else if (properties.getEffectiveAngle() > 0) {
							float angle = properties.getEffectiveAngle() / 2.0F;
							if (properties.isBackDirection()) {
								angle = 180.0F - angle;
							}
							FloatRange range = new FloatRange(angle, 360.0F - angle);
							if (range.containsFloat(PositionUtil.getAngleToTarget(firstTarget, nextCreature))) {
								if (MathUtil.isIn3dRange(firstTarget, nextCreature, distance + firstTarget.getObjectTemplate().getBoundRadius().getCollision())) {
									if (skill.shouldAffectTarget(nextCreature)) {
										skill.getEffectedList().add((Creature) nextCreature);
									}
								}
							}
						} else if (MathUtil.isIn3dRange(firstTarget, nextCreature, distance + firstTarget.getObjectTemplate().getBoundRadius().getCollision())) {
							if (skill.shouldAffectTarget(nextCreature)) {
								skill.getEffectedList().add((Creature) nextCreature);
							}
						}
					}
				}
			break;
			case PARTY:
				if (maxcount == 1) {
					break;
				}
				int partyCount = 0;
				if (skill.getEffector() instanceof Player) {
					Player effector = (Player) skill.getEffector();
					if (effector.isInAlliance2()) {
						effectedList.clear();
						for (Player player : effector.getPlayerAllianceGroup2().getMembers()) {
							if (partyCount >= 6 || partyCount >= maxcount) {
								break;
							} if (!player.isOnline()) {
								continue;
							} if (player.getLifeStats().isAlreadyDead()) {
								continue;
							} if (MathUtil.isIn3dRange(effector, player, distance + 5)) {
								effectedList.add(player);
								partyCount++;
							}
						}
					} else if (effector.isInGroup2()) {
						effectedList.clear();
						for (Player member : effector.getPlayerGroup2().getMembers()) {
							if (partyCount >= maxcount) {
								break;
							} if (!member.isOnline()) {
								continue;
							} if (member.getLifeStats().isAlreadyDead()) {
								continue;
							} if (MathUtil.isIn3dRange(effector, member, distance + 5)) {
								effectedList.add(member);
								partyCount++;
							}
						}
					}
				}
			break;
			case PARTY_WITHPET:
				if (skill.getEffector() instanceof Player) {
					final Player effector = (Player) skill.getEffector();
					if (effector.isInAlliance2()) {
						effectedList.clear();
						for (Player player : effector.getPlayerAlliance2().getMembers()) {
							if (!player.isOnline()) {
								continue;
							} if (player.getLifeStats().isAlreadyDead()) {
								continue;
							} if (MathUtil.isIn3dRange(effector, player, distance + 5)) {
								effectedList.add(player);
								Summon aMemberSummon = player.getSummon();
								if (aMemberSummon != null) {
									effectedList.add(aMemberSummon);
								}
							}
						}
					} else if (effector.isInGroup2()) {
						effectedList.clear();
						for (Player member : effector.getPlayerGroup2().getMembers()) {
							if (!member.isOnline()) {
								continue;
							} if (member.getLifeStats().isAlreadyDead()) {
								continue;
							} if (MathUtil.isIn3dRange(effector, member, distance + 5)) {
								effectedList.add(member);
								Summon aMemberSummon = member.getSummon();
								if (aMemberSummon != null) {
									effectedList.add(aMemberSummon);
								}
							}
						}
					}
				}
			break;
			case POINT:
				for (VisibleObject nextCreature : skill.getEffector().getKnownList().getKnownObjects().values()) {
					if (!(nextCreature instanceof Creature)) {
						continue;
					} if (((Creature) nextCreature).getLifeStats().isAlreadyDead()) {
						continue;
					} if ((nextCreature instanceof Player) && (((Player) nextCreature).isProtectionActive())) {
						continue;
					} if (MathUtil.getDistance(skill.getX(), skill.getY(), skill.getZ(), nextCreature.getX(), nextCreature.getY(), nextCreature.getZ()) <= distance + 5) {
						effectedList.add((Creature) nextCreature);
					}
				}
			case NONE:
			break;
		}
		return true;
	}
}