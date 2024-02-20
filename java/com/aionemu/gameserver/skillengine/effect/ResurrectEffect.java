package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_RESURRECT_BY_OTHER;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResurrectEffect")
public class ResurrectEffect extends EffectTemplate
{
	@XmlAttribute(name = "skill_id")
	protected int skillId;
	
	@Override
    public void applyEffect(Effect effect) {
        Player effectedPlayer = (Player) effect.getEffected();
        effectedPlayer.setPlayerResActivate(true);
        effectedPlayer.setResurrectionSkill(skillId);
        PacketSendUtility.sendPacket(effectedPlayer, new S_RESURRECT_BY_OTHER(effect.getEffector(), effect.getSkillId()));
    }
	
	@Override
    public void calculate(Effect effect) {
        if (effect.getEffected() instanceof Player && effect.getEffected().getLifeStats().isAlreadyDead()) {
            super.calculate(effect, null, null);
        }
    }
}