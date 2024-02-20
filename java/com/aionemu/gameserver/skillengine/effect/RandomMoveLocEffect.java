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
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.skillengine.model.DashStatus;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.SkillMoveType;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.geo.GeoService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/****/
/** Author Rinzler (Encom)
/****/

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RandomMoveLocEffect")
public class RandomMoveLocEffect extends EffectTemplate
{
	@XmlAttribute(name = "distance")
	private float distance;
	
	@XmlAttribute(name = "direction")
	private float direction;
	
	private VisibleObject effectorTarget = null;
	
	@Override
	public void applyEffect(Effect effect) {
		final Player effector = (Player) effect.getEffector();
		Skill skill = effect.getSkill();
		World.getInstance().updatePosition(effector, skill.getX(), skill.getY(), skill.getZ(), skill.getH());
		if (effectorTarget != null && !effectorTarget.getObjectId().equals(effector.getObjectId())) {
            effector.setTarget(effectorTarget);
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    PacketSendUtility.sendPacket(effector, new S_TARGET_INFO(effector));
                }
            }, 200);
        }
	}
	
	@Override
	public void calculate(Effect effect) {
		effect.addSucessEffect(this);
        SkillMoveType smt = this.direction == 1.0f ? SkillMoveType.MOVEBEHIND : SkillMoveType.DODGE;
		effect.setSkillMoveType(smt);
		effect.setDashStatus(DashStatus.RANDOMMOVELOC);
		final Player effector = (Player) effect.getEffector();
		final Creature effected = effect.getEffected();
		if (effector.getTarget() != null) {
            effectorTarget = effector.getTarget();
        } if (effectorTarget != null && !effectorTarget.getObjectId().equals(effector.getObjectId())) {
            effector.setTarget(null);
            PacketSendUtility.sendPacket(effector, new S_TARGET_INFO(effector));
        } if (distance != 0.0f) {
			double radian = Math.toRadians(MathUtil.convertHeadingToDegree(effector.getHeading()));
			float x1 = (float) (Math.cos(Math.PI * direction + radian) * distance);
			float y1 = (float) (Math.sin(Math.PI * direction + radian) * distance);
			byte intentions = (byte) (CollisionIntention.PHYSICAL.getId() | CollisionIntention.DOOR.getId());
			Vector3f closestCollision = GeoService.getInstance().getClosestCollision(effector, effector.getX() + x1, effector.getY() + y1, effector.getZ() - 0.1f, false, intentions);
			if (effected.isFlying()) {
                closestCollision.setZ(effected.getZ() - 0.1f);
            }
			effector.getEffectController().updatePlayerEffectIcons();
			PacketSendUtility.broadcastPacketAndReceive(effector, new S_POLYMORPH(effector, true));
			PacketSendUtility.broadcastPacketAndReceive(effector, new S_POLYMORPH(effector, effector.getTransformedModelId(), true, effector.getTransformedItemId()));
			effect.getSkill().setTargetPosition(closestCollision.getX(), closestCollision.getY(), closestCollision.getZ(), effector.getHeading());
		} else {
            effect.getSkill().setTargetPosition(effector.getX(), effector.getY(), effector.getZ(), effector.getHeading());
        }
	}
}