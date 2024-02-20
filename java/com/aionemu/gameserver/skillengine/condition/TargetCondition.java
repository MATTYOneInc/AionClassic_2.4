/*
 * This file is part of aion-unique <aion-unique.com>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.skillengine.condition;

import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.properties.FirstTargetAttribute;
import com.aionemu.gameserver.skillengine.properties.TargetRangeAttribute;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TargetCondition")
public class TargetCondition extends Condition {

	@XmlAttribute(required = true)
	protected TargetAttribute value;

	/**
	 * Gets the value of the value property.
	 * 
	 * @return possible object is {@link TargetAttribute }
	 */
	public TargetAttribute getValue() {
		return value;
	}

	@Override
	public boolean validate(Skill skill) {
		if ((value == TargetAttribute.NONE) || (value == TargetAttribute.ALL))
			return true;
		if (skill.getSkillTemplate().getProperties().getTargetType().equals(TargetRangeAttribute.AREA))
			return true;
		if ((skill.getSkillTemplate().getProperties().getFirstTarget() != FirstTargetAttribute.TARGET) && (skill.getSkillTemplate().getProperties().getFirstTarget() != FirstTargetAttribute.TARGETORME)) {
			return true;
		}
		if ((skill.getSkillTemplate().getProperties().getFirstTarget() == FirstTargetAttribute.TARGETORME) && (skill.getEffector() == skill.getFirstTarget())) {
			return true;
		}
		boolean result = false;
		switch (value) {
			case NPC:
				result = skill.getFirstTarget() instanceof Npc;
				break;
			case PC:
				result = skill.getFirstTarget() instanceof Player;
		}

		if ((!result) && ((skill.getEffector() instanceof Player))) {
			PacketSendUtility.sendPacket((Player) skill.getEffector(), S_MESSAGE_CODE.STR_SKILL_TARGET_IS_NOT_VALID);
		}
		return result;
	}
}
