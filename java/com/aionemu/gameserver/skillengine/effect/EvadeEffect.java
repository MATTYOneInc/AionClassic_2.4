package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillMoveType;
import com.aionemu.gameserver.skillengine.model.SpellStatus;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EvadeEffect")
public class EvadeEffect extends DispelEffect
{
	@Override
    public void calculate(Effect effect) {
		final Player effector = (Player) effect.getEffector();
        effect.setSkillMoveType(SkillMoveType.MOVEBEHIND);
        if (effect.getEffected().getState() == 3) {
            super.calculate(effect, null, null);
        } else {
            super.calculate(effect, null, SpellStatus.CLOSEAERIAL);
        } if (effector.isFlying()) {
            effector.setFlyState(0);
        }
		effector.getEffectController().updatePlayerEffectIcons();
		PacketSendUtility.broadcastPacketAndReceive(effector, new S_POLYMORPH(effector, true));
		PacketSendUtility.broadcastPacketAndReceive(effector, new S_POLYMORPH(effector, effector.getTransformedModelId(), true, effector.getTransformedItemId()));
    }
}