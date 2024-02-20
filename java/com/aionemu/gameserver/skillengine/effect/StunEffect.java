package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SpellStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StunEffect")
public class StunEffect extends EffectTemplate
{
	@Override
	public void applyEffect(Effect effect) {
		effect.addToEffectedController();
	}
	
	@Override
	public void calculate(Effect effect) {
		super.calculate(effect, StatEnum.STUN_RESISTANCE, null);
	}
	
	@Override
	public void startEffect(final Effect effect) {
		final Creature effected = effect.getEffected();
		if (effected.isInState(CreatureState.RESTING)) {
        	effected.unsetState(CreatureState.RESTING);
		} if (effected instanceof Player && effected.isInState(CreatureState.GLIDING)) {
            ((Player) effected).getFlyController().endFly(true);
        }
		effected.getMoveController().abortMove();
		effected.getController().cancelCurrentSkill();
		effected.getEffectController().setAbnormal(AbnormalState.STUN.getId());
		effect.setAbnormal(AbnormalState.STUN.getId());
    }
	
	@Override
	public void endEffect(Effect effect) {
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.STUN.getId());
	}
}