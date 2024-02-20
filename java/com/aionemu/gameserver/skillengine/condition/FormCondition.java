package com.aionemu.gameserver.skillengine.condition;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.TransformType;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FormCondition")
public class FormCondition extends Condition {

	@XmlAttribute(required = true)
	protected TransformType value;

	public boolean validate(Skill env) {
		if ((env.getEffector() instanceof Player)) {
			if ((env.getEffector().getTransformModel().isActive()) && (env.getEffector().getTransformModel().getType() == value)) {
				return true;
			}
			PacketSendUtility.sendPacket((Player) env.getEffector(), S_MESSAGE_CODE.STR_SKILL_CAN_NOT_CAST_IN_THIS_FORM);
			return false;
		}

		return true;
	}
}
