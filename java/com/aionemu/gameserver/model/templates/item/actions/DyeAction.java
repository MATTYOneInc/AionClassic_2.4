/*
 * This file is part of aion-unique <www.aion-unique.org>.
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
package com.aionemu.gameserver.model.templates.item.actions;

import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.network.aion.serverpackets.S_WIELD;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DyeAction")
public class DyeAction extends AbstractItemAction
{
	@XmlAttribute(name = "color")
	public String color;
	
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		if (targetItem == null) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_ITEM_COLOR_ERROR);
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
		if (!player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1)) {
			return;
		} if (color.equals("no")) {
			targetItem.setItemColor(0);
		} else {
			int rgb = Integer.parseInt(color, 16);
			int bgra = 0xFF | ((rgb & 0xFF) << 24) | ((rgb & 0xFF00) << 8) | ((rgb & 0xFF0000) >>> 8);
			targetItem.setItemColor(bgra);
			ItemPacketService.updateItemAfterInfoChange(player, targetItem);
			PacketSendUtility.broadcastPacket(player, new S_WIELD(player.getObjectId(), player.getEquipment().getEquippedItemsWithoutStigma()), true);
			player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
		}
	}
}