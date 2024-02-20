/*
 *  Aion Classic Emu based on Aion Encom Source Files
 *
 *  ENCOM Team based on Aion-Lighting Open Source
 *  All Copyrights : "Data/Copyrights/AEmu-Copyrights.text
 *
 *  iMPERIVM.FUN - AION DEVELOPMENT FORUM
 *  Forum: <http://https://imperivm.fun/>
 *
 */
package com.aionemu.gameserver.model.templates.item.actions;

import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.effect.EffectTemplate;
import com.aionemu.gameserver.skillengine.effect.SummonEffect;
import com.aionemu.gameserver.skillengine.effect.TransformEffect;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SkillUseAction")
public class SkillUseAction extends AbstractItemAction
{
	@XmlAttribute
	protected int skillid;
	
	@XmlAttribute
	protected int level;
	
	public int getSkillid() {
		return skillid;
	}
	
	public int getLevel() {
		return level;
	}
	
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		Skill skill = SkillEngine.getInstance().getSkill(player, skillid, level, player.getTarget(), parentItem.getItemTemplate());
        if (skill == null) {
            return false;
        }
		int nameId = parentItem.getItemTemplate().getNameId();
        byte levelRestrict = parentItem.getItemTemplate().getMaxLevelRestrict(player);
        if (levelRestrict != 0) {
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_USE_ITEM_TOO_LOW_LEVEL_MUST_BE_THIS_LEVEL(levelRestrict, nameId));
            return false;
        } if (player.isTransformed()) {
			for (EffectTemplate template: skill.getSkillTemplate().getEffects().getEffects()) {
				if (template instanceof TransformEffect) {
					///You cannot use this skill while transformed.
					PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_SKILL_CAN_NOT_CAST_IN_SHAPECHANGE, 0);
					return false;
				}
			}
		} if (player.getSummon() != null) {
			for (EffectTemplate template: skill.getSkillTemplate().getEffects().getEffects()) {
				if (template instanceof SummonEffect) {
					///You already have a spirit following you.
					PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_SKILL_SUMMON_ALREADY_HAVE_A_FOLLOWER, 0);
					return false;
				}
			}
		} if (player.getEffectController().hasAbnormalEffect(skillid)) {
            ///You are already under the same effect.
			PacketSendUtility.playerSendPacketTime(player, S_MESSAGE_CODE.STR_ITEM_SAME_EFFECT_ALREADY_TAKEN, 0);
            return false;
        }
		return skill.canUseSkill();
	}
	
	@Override
	public void act(final Player player, final Item parentItem, Item targetItem) {
		final Skill skill = SkillEngine.getInstance().getSkill(player, skillid, level, player.getTarget(), parentItem.getItemTemplate());
		if (skill != null) {
			player.getController().cancelUseItem();
			player.setUsingItem(parentItem);
			skill.setItemObjectId(parentItem.getObjectId());
			skill.useSkill();
			QuestEnv env = new QuestEnv(player.getTarget(), player, 0, 0);
			QuestEngine.getInstance().onUseSkill(env, skillid);
		}
	}
}