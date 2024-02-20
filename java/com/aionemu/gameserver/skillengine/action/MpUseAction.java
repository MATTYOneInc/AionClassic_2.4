package com.aionemu.gameserver.skillengine.action;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.utils.PacketSendUtility;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MpUseAction")
public class MpUseAction extends Action
{
	@XmlAttribute(required = true)
	protected int value;
	
	@XmlAttribute
	protected int delta;
	
	@XmlAttribute
	protected boolean ratio;
	
	@Override
	public void act(Skill skill) {
		Creature effector = skill.getEffector();
		int currentMp = effector.getLifeStats().getCurrentMp();
		int valueWithDelta = value + delta * skill.getSkillLevel();
		if (ratio) {
			valueWithDelta = (skill.getEffector().getLifeStats().getMaxMp() * valueWithDelta) / 100;
		}
		int changeMpPercent = skill.getBoostSkillCost();
		if (changeMpPercent != 0) {
			valueWithDelta = valueWithDelta - ((valueWithDelta / ((100 / changeMpPercent))));
		} if (effector instanceof Player) {
			if (currentMp <= 0 || currentMp < valueWithDelta) {
				PacketSendUtility.sendPacket((Player) effector, S_MESSAGE_CODE.STR_SKILL_NOT_ENOUGH_MP);
				return;
			}
		}
		effector.getLifeStats().reduceMp(valueWithDelta);
	}
}