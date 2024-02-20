package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.S_MOVEBACK;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillMoveType;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PulledEffect")
public class PulledEffect extends EffectTemplate
{
	@Override
	public void applyEffect(Effect effect) {
		final Creature effected = effect.getEffected();
		if (!effect.getEffected().getEffectController().isAbnormalSet(AbnormalState.CANNOT_MOVE) &&
		    ///Stone Skin I-V
			!effect.getEffected().getEffectController().hasAbnormalEffect(1377) ||
			!effect.getEffected().getEffectController().hasAbnormalEffect(1378) ||
			!effect.getEffected().getEffectController().hasAbnormalEffect(1379) ||
			!effect.getEffected().getEffectController().hasAbnormalEffect(1380) ||
			!effect.getEffected().getEffectController().hasAbnormalEffect(1390)) {
			effect.addToEffectedController();
			effected.setPulledMulti(0);
			effected.getController().cancelCurrentSkill();
			effect.setIsPhysicalState(true);
			World.getInstance().updatePosition(effected, effect.getTargetX(), effect.getTargetY(), effect.getTargetZ(), effected.getHeading());
			PacketSendUtility.broadcastPacketAndReceive(effected, new S_MOVEBACK(effect.getEffector(), effected.getObjectId(), effect.getTargetX(), effect.getTargetY(), effect.getTargetZ()));
		}
	}
	
	@Override
	public void calculate(Effect effect) {
		if (effect.getEffected().getEffectController().hasPhysicalStateEffect()) {
			return;
		} if (!super.calculate(effect, StatEnum.PULLED_RESISTANCE, null)) {
			return;
		}
		effect.addSucessEffect(this);
		effect.setSkillMoveType(SkillMoveType.PULL);
		final Creature effector = effect.getEffector();
		double radian = Math.toRadians(MathUtil.convertHeadingToDegree(effector.getHeading()));
		final float x1 = (float) Math.cos(radian);
		final float y1 = (float) Math.sin(radian);
		effect.setTragetLoc(effector.getX() + x1, effector.getY() + y1, effector.getZ());
	}
	
	@Override
	public void startEffect(Effect effect) {
		final Creature effected = effect.getEffected();
		effected.getEffectController().setAbnormal(AbnormalState.CANNOT_MOVE.getId());
		effect.setAbnormal(AbnormalState.CANNOT_MOVE.getId());
	}
	
	@Override
	public void endEffect(Effect effect) {
		effect.setIsPhysicalState(false);
		effect.getEffected().setPulledMulti(1);
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.CANNOT_MOVE.getId());
	}
}