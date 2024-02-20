package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.geoEngine.collision.CollisionIntention;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.skillengine.model.DashStatus;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.world.geo.GeoService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BackDashEffect")
public class BackDashEffect extends DamageEffect
{
	@XmlAttribute(name = "distance")
	private float distance;
	
	private float direction = 1.0f;
	
	@Override
	public void calculate(Effect effect) {
		final Player effector = (Player) effect.getEffector();
		effect.addSucessEffect(this);
		effect.setDashStatus(DashStatus.BACKDASH);
		byte h = effector.getHeading();
        if (PositionUtil.isBehind(effector, effector.getTarget())) {
            h = (byte)(h - 60);
        }
		double radian = Math.toRadians(MathUtil.convertHeadingToDegree(h));
        float x1 = (float) (Math.cos(Math.PI * direction + radian) * distance);
		float y1 = (float) (Math.sin(Math.PI * direction + radian) * distance);
		effector.getEffectController().updatePlayerEffectIcons();
		PacketSendUtility.broadcastPacketAndReceive(effector, new S_POLYMORPH(effector, true));
		PacketSendUtility.broadcastPacketAndReceive(effector, new S_POLYMORPH(effector, effector.getTransformedModelId(), true, effector.getTransformedItemId()));
		PacketSendUtility.sendPacket(effector, new S_CUSTOM_ANIM(effector.getObjectId(), effector.getMotions().getActiveMotions()));
		byte intentions = (byte) (CollisionIntention.PHYSICAL.getId() | CollisionIntention.DOOR.getId());
        Vector3f closestCollision = GeoService.getInstance().getClosestCollision(effector, effector.getX() + x1, effector.getY() + y1, effector.getZ(), false, intentions);
		effect.getSkill().setTargetPosition(closestCollision.getX(), closestCollision.getY(), closestCollision.getZ(), effector.getHeading());
		super.calculate(effect);
	}
}