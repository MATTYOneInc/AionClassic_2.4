package com.aionemu.gameserver.skillengine.action;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.utils.PacketSendUtility;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DpUseAction")
public class DpUseAction extends Action
{
	@XmlAttribute(required = true)
	protected int value;
	
	@Override
	public void act(Skill skill) {
		final Player effector = (Player) skill.getEffector();
		int currentDp = effector.getCommonData().getDp();
		if (currentDp <= 0 || currentDp < value) {
			PacketSendUtility.sendPacket(effector, S_MESSAGE_CODE.STR_SKILL_NOT_ENOUGH_DP);
			return;
		}
		effector.getCommonData().setDp(currentDp - value);
	}
}