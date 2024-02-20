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
@XmlType(name = "StaggerEffect")
public class StaggerEffect extends EffectTemplate
{
	@Override
    public void applyEffect(Effect effect) {
        if (!effect.getEffected().getEffectController().hasPhysicalStateEffect() && !effect.getEffected().getEffectController().isAbnormalSet(AbnormalState.CANNOT_MOVE)) {
            effect.addToEffectedController();
			effect.setIsPhysicalState(true);
            final Creature effected = effect.getEffected();
            if (effected instanceof Player && effected.isInState(CreatureState.GLIDING)) {
                ((Player) effected).getFlyController().endFly(true);
            }
            effected.getController().cancelCurrentSkill();
            effected.getEffectController().removeParalyzeEffects();
            effected.getMoveController().abortMove();
            PacketSendUtility.broadcastPacketAndReceive(effect.getEffected(), new S_MOVEBACK(effect.getEffector(), effect.getEffected().getObjectId(), effect.getTargetX(), effect.getTargetY(), effect.getTargetZ()));
            World.getInstance().updatePosition(effected, effect.getTargetX(), effect.getTargetY(), effect.getTargetZ(), effected.getHeading());
        }
    }
	
	@Override
	public void startEffect(Effect effect) {
		effect.getEffected().getEffectController().setAbnormal(AbnormalState.STAGGER.getId());
		effect.setAbnormal(AbnormalState.STAGGER.getId());
	}
	
	@Override
	public void calculate(Effect effect) {
		if (effect.getEffected().getEffectController().hasPhysicalStateEffect()) {
			return;
		} if (!super.calculate(effect, StatEnum.STAGGER_RESISTANCE, SpellStatus.STAGGER)) {
			return;
		}
		effect.addSucessEffect(this);
		effect.setSkillMoveType(SkillMoveType.STAGGER);
		final Creature effector = effect.getEffector();
		final Creature effected = effect.getEffected();
		effected.getController().cancelCurrentSkill();
		double radian = Math.toRadians(MathUtil.convertHeadingToDegree(effector.getHeading()));
		float x1 = (float) (Math.cos(radian) * 3);
		float y1 = (float) (Math.sin(radian) * 3);
		effect.setTragetLoc(effected.getX() + x1, effected.getY() + y1, effected.getZ());
	}
	
	@Override
	public void endEffect(Effect effect) {
		effect.setIsPhysicalState(false);
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.STAGGER.getId());
	}
}