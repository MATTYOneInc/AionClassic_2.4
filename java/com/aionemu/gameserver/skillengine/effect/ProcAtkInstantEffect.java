package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_HIT_POINT_OTHER.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.S_HIT_POINT_OTHER.TYPE;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.skillengine.action.DamageType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProcAtkInstantEffect")
public class ProcAtkInstantEffect extends DamageEffect
{
	@Override
    public void applyEffect(Effect effect) {
        if (effect.getEffected() != effect.getEffector() && effect.getEffector() instanceof Player) {
			//%0 has been activated.
            PacketSendUtility.sendPacket((Player) effect.getEffector(), new S_MESSAGE_CODE(1301062, new DescriptionId(effect.getSkillTemplate().getNameId())));
        }
        effect.getEffected().getController().onAttack(effect.getEffector(), effect.getSkillId(), TYPE.DAMAGE, effect.getReserved1(), false, LOG.PROCATKINSTANT);
    }
	
    @Override
    public void calculate(Effect effect) {
        super.calculate(effect, DamageType.MAGICAL);
    }
}