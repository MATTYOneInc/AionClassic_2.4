package com.aionemu.gameserver.model.templates.item.actions;

import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.S_USE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TitleAddAction")
public class TitleAddAction extends AbstractItemAction
{
	@XmlAttribute
	protected int titleid;
	
	@XmlAttribute
	protected Integer minutes;
	
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		if (titleid == 0 || parentItem == null) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_ITEM_COLOR_ERROR);
			return false;
		} if (player.getTitleList().contains(titleid)) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_TOOLTIP_LEARNED_TITLE);
			return false;
		} if (player.getController().isInCombat() || player.isAttackMode()) {
			///You cannot use %1 while in combat.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_SKILL_ITEM_RESTRICTED_AREA(new DescriptionId(2800159), parentItem.getNameId()));
			return false;
		}
		return true;
	}
	
	@Override
	public void act(Player player, Item parentItem, Item targetItem) {
		ItemTemplate itemTemplate = parentItem.getItemTemplate();
		PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_USE_ITEM(new DescriptionId(itemTemplate.getNameId())));
		PacketSendUtility.broadcastPacket(player, new S_USE_ITEM(player.getObjectId(), parentItem.getObjectId(), itemTemplate.getTemplateId()), true);
		if (player.getTitleList().addTitle(titleid, false, minutes == null ? 0 : ((int)(System.currentTimeMillis()/1000)) + minutes * 60)) {
			Item item = player.getInventory().getItemByObjId(parentItem.getObjectId());
			player.getInventory().delete(item);
		}
	}
}