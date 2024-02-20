package com.aionemu.gameserver.skillengine.properties;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.properties.Properties.CastState;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.geo.GeoService;

public class FirstTargetRangeProperty
{
	public static boolean set(Skill skill, Properties properties, CastState castState) {
		float firstTargetRange = properties.getFirstTargetRange();
		if (!skill.isFirstTargetRangeCheck()) {
			return true;
		}
		Creature effector = skill.getEffector();
		Creature firstTarget = skill.getFirstTarget();
		if (firstTarget == null) {
			return false;
		} if (properties.isAddWeaponRange()) {
			firstTargetRange += skill.getEffector().getGameStats().getAttackRange().getCurrent() / 1000f;
		} if (!castState.isCastStart()) {
			firstTargetRange += properties.getRevisionDistance();
		} if (firstTarget.getObjectId() == effector.getObjectId()) {
			return true;
		} if (!MathUtil.isInAttackRange(effector, firstTarget, firstTargetRange + 2)) {
			if (effector instanceof Player) {
				PacketSendUtility.sendPacket((Player) effector, S_MESSAGE_CODE.STR_ATTACK_TOO_FAR_FROM_TARGET);
			}
			return false;
		} if (skill.getSkillTemplate().getSkillId() != 1606) {
			if (!GeoService.getInstance().canSee(effector, firstTarget)) {
				if (effector instanceof Player) {
					PacketSendUtility.sendPacket((Player) effector, S_MESSAGE_CODE.STR_SKILL_OBSTACLE);
				}
				return false;
			}
		}
		return true;
	}
}