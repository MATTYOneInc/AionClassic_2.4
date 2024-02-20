package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.S_MOVEBACK;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillMoveType;
import com.aionemu.gameserver.skillengine.model.SpellStatus;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StumbleEffect")
public class StumbleEffect extends EffectTemplate
{
	@Override
    public void applyEffect(Effect effect) {
        if (!effect.getEffected().getEffectController().isAbnormalSet(AbnormalState.OPENAERIAL) &&
		    !effect.getEffected().getEffectController().isAbnormalSet(AbnormalState.CANNOT_MOVE)) {
            effect.addToEffectedController();
			effect.setIsPhysicalState(true);
            final Creature effected = effect.getEffected();
            if (effected instanceof Player && effected.isInState(CreatureState.GLIDING)) {
                ((Player) effected).getFlyController().endFly(true);
            }
            effected.getController().cancelCurrentSkill();
            effected.getEffectController().removeParalyzeEffects();
            effected.getMoveController().abortMove();
            World.getInstance().updatePosition(effected, effect.getTargetX(), effect.getTargetY(), effect.getTargetZ(), effected.getHeading());
            PacketSendUtility.broadcastPacketAndReceive(effect.getEffected(), new S_MOVEBACK(effect.getEffector(), effect.getEffected().getObjectId(), effect.getTargetX(), effect.getTargetY(), effect.getTargetZ()));
        }
    }
	
	@Override
	public void startEffect(Effect effect) {
		if (effect.getEffected().getEffectController().isAbnormalSet(AbnormalState.OPENAERIAL)) {
			effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.OPENAERIAL.getId());
		}
		effect.getEffected().getEffectController().setAbnormal(AbnormalState.STUMBLE.getId());
		effect.setAbnormal(AbnormalState.STUMBLE.getId());
	}
	
	@Override
	public void calculate(Effect effect) {
		if (effect.getEffected().getEffectController().hasPhysicalStateEffect()) {
			return;
		} if (!super.calculate(effect, StatEnum.STUMBLE_RESISTANCE, SpellStatus.STUMBLE)) {
			return;
		}
		effect.addSucessEffect(this);
		effect.setSkillMoveType(SkillMoveType.STUMBLE);
		final Creature effector = effect.getEffector();
		final Creature effected = effect.getEffected();
		effected.getController().cancelCurrentSkill();
		float direction = effected instanceof Player ? 1.5f : 0.5f;
		double radian = Math.toRadians(MathUtil.convertHeadingToDegree(effector.getHeading()));
		float x1 = (float) (Math.cos(radian) * direction);
		float y1 = (float) (Math.sin(radian) * direction);
		effect.setTragetLoc(effected.getX() + x1, effected.getY() + y1, effected.getZ());
	}
	
	@Override
	public void endEffect(Effect effect) {
		effect.setIsPhysicalState(false);
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.STUMBLE.getId());
	}
}