package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.network.aion.serverpackets.S_FORCE_BLINK;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SpellStatus;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SnareEffect")
public class SnareEffect extends BuffEffect
{
	@Override
	public void applyEffect(Effect effect) {
		if (!effect.getEffected().getEffectController().isAbnormalSet(AbnormalState.CANNOT_MOVE)) {
            effect.addToEffectedController();
        }
	}
	
	@Override
	public void calculate(Effect effect) {
		super.calculate(effect, StatEnum.SNARE_RESISTANCE, SpellStatus.SNARE);
	}
	
	@Override
	public void startEffect(Effect effect) {
		super.startEffect(effect);
		final Creature effected = effect.getEffected();
		effected.getEffectController().setAbnormal(AbnormalState.SNARE.getId());
		effect.setAbnormal(AbnormalState.SNARE.getId());
        if (effect.getEffected().isFlying() || effect.getEffected().isInState(CreatureState.GLIDING)) {
            PacketSendUtility.broadcastPacketAndReceive(effected, new S_FORCE_BLINK(effected));
            effected.getMoveController().abortMove();
        }
    }
	
	@Override
	public void endEffect(Effect effect) {
		super.endEffect(effect);
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.SNARE.getId());
	}
}