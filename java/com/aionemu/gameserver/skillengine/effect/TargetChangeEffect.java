package com.aionemu.gameserver.skillengine.effect;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_TARGET_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_USER_CHANGED_TARGET;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TargetChangeEffect")
public class TargetChangeEffect extends EffectTemplate
{
	@Override
    public void calculate(Effect effect) {
        effect.addSucessEffect(this);
    }
	
	@Override
    public void applyEffect(Effect effect) {
        effect.addToEffectedController();
        Creature effected = effect.getEffected();
		if (effected instanceof Player) {
			if (effect.getSkillId() >= 1 && effect.getSkillId() <= 99999) {
				int random = Rnd.get(0, 1000);
				if (random < 250) {
					if (effect.getEffector() != null) {
						effected.setTarget(effect.getEffector());
						PacketSendUtility.sendPacket((Player) effected, new S_TARGET_INFO((Player) effected));
						PacketSendUtility.broadcastPacket(effected, new S_USER_CHANGED_TARGET((Player) effected));
					}
				}
			} else {
				effected.setTarget(null);
				PacketSendUtility.sendPacket((Player) effected, new S_TARGET_INFO(null));
				PacketSendUtility.broadcastPacket(effected, new S_USER_CHANGED_TARGET((Player) effected));
			}
		}
    }
}