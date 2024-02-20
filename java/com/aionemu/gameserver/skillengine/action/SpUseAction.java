package com.aionemu.gameserver.skillengine.action;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.skillengine.model.Skill;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpUseAction")
public class SpUseAction extends Action
{
	@XmlAttribute(required = true)
	protected int value;
	
	@Override
	public void act(Skill skill) {
		final Player effector = (Player) skill.getEffector();
		int currentSp = 0; //effector.getCommonData().getSp();
		if (currentSp <= 0 || currentSp < value) {
			return;
		}
		//effector.getCommonData().setSp(currentSp - value);
	}
}