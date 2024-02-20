package com.aionemu.gameserver.skillengine.condition;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.utils.PacketSendUtility;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrowCheckCondition")
public class ArrowCheckCondition extends Condition
{
	@Override
	public boolean validate(Skill skill) {
		if (skill.getEffector() instanceof Player) {
			Player player = (Player)skill.getEffector();
			if (player.getEquipment().isArrowEquipped()) {
				return true;
			}
            ///You cannot attack because you have no arrow.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANT_ATTACK_NO_ARROW);
			return false;
		} else {
			return true;
		}
	}
}