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
@XmlType(name = "HpUseAction")
public class HpUseAction extends Action
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
		int valueWithDelta = value + delta * skill.getSkillLevel();
		int currentHp = effector.getLifeStats().getCurrentHp();
		if (ratio) {
			valueWithDelta = (int) (valueWithDelta / 100f * skill.getEffector().getLifeStats().getMaxHp());
		} if (effector instanceof Player) {
			if (currentHp <= 0 || currentHp < valueWithDelta) {
				PacketSendUtility.sendPacket((Player) effector, S_MESSAGE_CODE.STR_SKILL_NOT_ENOUGH_HP);
				return;
			}
		}
		effector.getLifeStats().reduceHp(valueWithDelta, effector);
	}
}