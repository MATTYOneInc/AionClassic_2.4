package com.aionemu.gameserver.services.item;

import com.aionemu.gameserver.configs.main.LegionConfig;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.team.legion.LegionPermissionsMask;
import com.aionemu.gameserver.model.templates.item.ItemCategory;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.legion.LegionService;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class ItemRestrictionService
{
	public static boolean isItemRestrictedFrom(Player player, Item item, byte storage) {
		StorageType type = StorageType.getStorageTypeById(storage);
		switch (type) {
			case LEGION_WAREHOUSE:
				if (!LegionService.getInstance().getLegionMember(player.getObjectId()).hasRights(LegionPermissionsMask.WH_WITHDRAWAL) ||
				    !LegionConfig.LEGION_WAREHOUSE || !player.isLegionMember()) {
					///You do not have the authority to use the Legion warehouse.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300322));
					return true;
				}
			break;
		}
		return false;
	}
	
	public static boolean isItemRestrictedTo(Player player, Item item, byte storage) {
		StorageType type = StorageType.getStorageTypeById(storage);
		switch (type) {
			case REGULAR_WAREHOUSE:
				if (!item.isStorableinWarehouse(player)) {
					///You cannot store this in the warehouse.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300418));
					return true;
				} else if (item.getItemTemplate().isMedal() || item.getItemTemplate().isAbyssItem()) {
					///You cannot store this in the warehouse.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300418));
					return true;
				}
			break;
			case ACCOUNT_WAREHOUSE:
				if (!item.isStorableinAccWarehouse(player)) {
					///You cannot store this item in the account warehouse.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400356));
					return true;
				} else if (item.getItemTemplate().isMedal() || item.getItemTemplate().isAbyssItem()) {
					///You cannot store this item in the account warehouse.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400356));
					return true;
				}
			break;
			case LEGION_WAREHOUSE:
				if (!item.isStorableinLegWarehouse(player) || !LegionConfig.LEGION_WAREHOUSE) {
					///You cannot store this item in the Legion warehouse.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400355));
					return true;
				} else if (item.getItemTemplate().isMedal() || item.getItemTemplate().isAbyssItem()) {
					///You cannot store this item in the Legion warehouse.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1400355));
					return true;
				} else if (!player.isLegionMember() || !LegionService.getInstance().getLegionMember(player.getObjectId()).hasRights(LegionPermissionsMask.WH_DEPOSIT)) {
					///You do not have the authority to use the Legion warehouse.
					PacketSendUtility.sendPacket(player, new S_MESSAGE_CODE(1300322));
					return true;
				}
			break;
		}
		return false;
	}
	
	public static boolean canRemoveItem(Player player, Item item) {
		ItemTemplate it = item.getItemTemplate();
		if (it.getCategory() == ItemCategory.QUEST) {
			return true;
		}
		return true;
	}
}