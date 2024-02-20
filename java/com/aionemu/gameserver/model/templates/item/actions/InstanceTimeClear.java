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

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_INSTANCE_DUNGEON_COOLTIMES;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAttribute;

public class InstanceTimeClear extends AbstractItemAction
{
    @XmlAttribute(name = "sync_ids")
    protected int sync;
	
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
    public void act(final Player player, final Item parentItem, Item targetItem) {
		int mapId = DataManager.INSTANCE_COOLTIME_DATA.getWorldId(sync);
		if (parentItem.getActivationCount() > 1) {
			parentItem.setActivationCount(parentItem.getActivationCount() - 1);
		} else {
			player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1);
		}
		player.getPortalCooldownList().removePortalCoolDown(mapId);
		///The zone has been reset. Once reset, you cannot enter the zone again until the reentry time expires.
		///You can check the reentry time by typing '/CheckEntry'.
		PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400043));
		PacketSendUtility.sendPacket(player, new S_INSTANCE_DUNGEON_COOLTIMES(player, true, player.getCurrentTeam()));
    }
}