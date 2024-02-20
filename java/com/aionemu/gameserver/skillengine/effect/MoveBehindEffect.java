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
package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.geoEngine.collision.CollisionIntention;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.skillengine.action.DamageType;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.DashStatus;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.SkillMoveType;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.geo.GeoService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/****/
/** Author Rinzler (Encom)
/****/

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MoveBehindEffect")
public class MoveBehindEffect extends DamageEffect
{
	@Override
    public void applyEffect(Effect effect) {
		Player effector = (Player) effect.getEffector();
		PacketSendUtility.sendPacket(effector, new S_USER_CHANGED_TARGET(effector));
		Skill skill = effect.getSkill();
		World.getInstance().updatePosition(effector, skill.getX(), skill.getY(), skill.getZ(), skill.getH());
    }
	
	@Override
    public void calculate(Effect effect) {
		final Player effector = (Player) effect.getEffector();
		final Creature effected = effect.getEffected();
		final ItemTemplate itemTemplate = effect.getItemTemplate();
		effect.addSucessEffect(this);
		effect.setSkillMoveType(SkillMoveType.MOVEBEHIND);
		effect.setDashStatus(DashStatus.MOVEBEHIND);
		double radian = Math.toRadians(MathUtil.convertHeadingToDegree(effected.getHeading()));
        float x1 = (float) (Math.cos(Math.PI + radian) * 1.3F);
        float y1 = (float) (Math.sin(Math.PI + radian) * 1.3F);
		float z = GeoService.getInstance().getZAfterMoveBehind(effected.getWorldId(), effected.getX() + x1, effected.getY() + y1, effected.getZ(), effected.getInstanceId());
		effector.getEffectController().updatePlayerEffectIcons();
		PacketSendUtility.broadcastPacketAndReceive(effector, new S_POLYMORPH(effector, true));
		PacketSendUtility.broadcastPacketAndReceive(effector, new S_POLYMORPH(effector, effector.getTransformedModelId(), true, effector.getTransformedItemId()));
		PacketSendUtility.sendPacket(effector, new S_CUSTOM_ANIM(effector.getObjectId(), effector.getMotions().getActiveMotions()));
		byte intentions = (byte) (CollisionIntention.PHYSICAL.getId() | CollisionIntention.DOOR.getId());
        Vector3f closestCollision = GeoService.getInstance().getClosestCollision(effector, effected.getX() + x1, effected.getY() + y1, z, false, intentions);
        effected.getMoveController().abortMove();
		if (itemTemplate != null) {
			SkillEngine.getInstance().getSkill(effector, effect.getSkillId(), 1, effected).useNoAnimationSkill();
		}
		effect.getSkill().setTargetPosition(closestCollision.getX(), closestCollision.getY(), closestCollision.getZ(), effected.getHeading());
		if (!super.calculate(effect, DamageType.PHYSICAL)) {
            return;
        }
    }
}