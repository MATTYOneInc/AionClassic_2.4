
package com.aionemu.gameserver.skillengine.condition;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Skill;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpCondition")
public class SpCondition extends Condition
{
	@XmlAttribute(required = true)
	protected int value;
	
	@Override
	public boolean validate(Skill skill) {
		return true;
		//return ((Player) skill.getEffector()).getCommonData().getSp() >= value;
	}
}