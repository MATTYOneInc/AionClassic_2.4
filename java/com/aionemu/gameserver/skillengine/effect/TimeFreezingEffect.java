package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TimeFreezingEffect")
public class TimeFreezingEffect extends EffectTemplate
{
	@Override
	public void applyEffect(Effect effect) {
		if (!effect.getEffected().getEffectController().hasMagicalStateEffect()) {
			effect.addToEffectedController();
			effect.setIsMagicalState(true);
		}
	}
	
	@Override
	public void calculate(Effect effect) {
		super.calculate(effect, StatEnum.PARALYZE_RESISTANCE, null);
	}
	
	@Override
	public void startEffect(Effect effect) {
		final Player effector = (Player) effect.getEffector();
		final Creature effected = effect.getEffected();
		effected.getController().cancelCurrentSkill();
		effected.getMoveController().abortMove();
		effect.setAbnormal(AbnormalState.PARALYZE.getId());
		effect.getEffected().getEffectController().setAbnormal(AbnormalState.PARALYZE.getId());
		//몸이 시간 제어됐습니다.
        PacketSendUtility.sendPacket(effector, S_MESSAGE_CODE.STR_SKILL_EFFECT_TIMEFREEZING_BEGIN);
	}
	
	@Override
	public void endEffect(Effect effect) {
		final Player effector = (Player) effect.getEffector();
		effect.setIsMagicalState(false);
		effect.getEffected().getEffectController().unsetAbnormal(AbnormalState.PARALYZE.getId());
		//몸의 시간 제어가 풀렸습니다.
        PacketSendUtility.sendPacket(effector, S_MESSAGE_CODE.STR_SKILL_EFFECT_TIMEFREEZING_END);
	}
}