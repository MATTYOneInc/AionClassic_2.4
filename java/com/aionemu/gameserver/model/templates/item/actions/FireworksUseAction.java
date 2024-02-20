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

import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FireworksUseAction")
public class FireworksUseAction extends AbstractItemAction
{
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		if (player.getController().isInCombat() || player.isAttackMode()) {
			///You cannot use %1 while in combat.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_SKILL_ITEM_RESTRICTED_AREA(new DescriptionId(2800159), parentItem.getNameId()));
			return false;
		}
		return true;
	}
	
	@Override
	public void act(Player player, Item parentItem, Item targetItem) {
		if (parentItem.getActivationCount() > 1) {
			parentItem.setActivationCount(parentItem.getActivationCount() - 1);
		} else {
			player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1);
		}
		PacketSendUtility.sendPacket(player, new S_USE_ITEM(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, 1, 0));
	}
}