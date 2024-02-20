package com.aionemu.gameserver.model.templates.item.actions;

import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SkillLearnAction")
public class SkillLearnAction extends AbstractItemAction
{
	@XmlAttribute
	protected int skillid;
	
	@XmlAttribute
	protected int level;
	
	@XmlAttribute(name = "class")
	protected PlayerClass playerClass;
	
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		if (player.getCommonData().getLevel() < level) {
			return false;
		}
		PlayerClass pc = player.getCommonData().getPlayerClass();
		if (!validateClass(pc)) {
			return false;
		}
		Race race = parentItem.getItemTemplate().getRace();
		if (player.getRace() != race && race != Race.PC_ALL) {
			return false;
		} if (player.getSkillList().isSkillPresent(skillid)) {
			///You have already learned this skill.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_SKILLLEARNBOOK_CANT_USE_ALREADY_HAS_SKILL);
			return false;
		} if (player.getController().isInCombat() || player.isAttackMode()) {
			///You cannot use %1 while in combat.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_SKILL_ITEM_RESTRICTED_AREA(new DescriptionId(2800159), parentItem.getNameId()));
			return false;
		}
		return true;
	}
	
	@Override
	public void act(final Player player, final Item parentItem, final Item targetItem) {
		ItemTemplate itemTemplate = parentItem.getItemTemplate();
		player.getController().cancelUseItem();
		PacketSendUtility.broadcastPacket(player, new S_USE_ITEM(player.getObjectId(), parentItem.getObjectId(), itemTemplate.getTemplateId()), true);
		SkillLearnService.learnSkillBook(player, skillid);
		player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1);
	}
	
	private boolean validateClass(PlayerClass pc) {
		boolean result = false;
		if (!pc.isStartingClass() && PlayerClass.getStartingClassFor(pc).ordinal() == playerClass.ordinal()) {
			result = true;
		} if (pc == playerClass || playerClass == PlayerClass.ALL) {
			result = true;
		}
		return result;
	}
}