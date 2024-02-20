package com.aionemu.gameserver.services;

import com.aionemu.gameserver.dao.InventoryDAO;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemQuality;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.services.item.ItemSocketService;
import com.aionemu.gameserver.services.trade.PricesService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArmsfusionService
{
    private static final Logger log = LoggerFactory.getLogger(ArmsfusionService.class);

    public static void fusionWeapons(Player player, int firstItemUniqueId, int secondItemUniqueId) {
        Item firstItem = player.getInventory().getItemByObjId(firstItemUniqueId);
        if (firstItem == null) {
            firstItem = player.getEquipment().getEquippedItemByObjId(firstItemUniqueId);
		}
        Item secondItem = player.getInventory().getItemByObjId(secondItemUniqueId);
        if (secondItem == null) {
            secondItem = player.getEquipment().getEquippedItemByObjId(secondItemUniqueId);
		} if (firstItem == null || secondItem == null) {
            return;
		}
        double priceRate = PricesService.getGlobalPrices(player.getRace()) * .01;
        double taxRate = PricesService.getTaxes(player.getRace()) * .01;
        double rarity = rarityRate(firstItem.getItemTemplate().getItemQuality());
        int priceMod = PricesService.getGlobalPricesModifier() * 2;
        int level = firstItem.getItemTemplate().getLevel();
        int price = (int) (priceMod * priceRate * taxRate * rarity * level * level);
        if (player.getInventory().getKinah() < price) {
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_COMPOUND_ERROR_NOT_ENOUGH_MONEY(firstItem.getNameId(), secondItem.getNameId()));
            return;
        } if (firstItem.hasFusionedItem()) {
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_COMPOUND_ERROR_NOT_AVAILABLE(firstItem.getNameId()));
            return;
        } if (secondItem.hasFusionedItem()) {
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_COMPOUND_ERROR_NOT_AVAILABLE(secondItem.getNameId()));
            return;
        } if (!firstItem.getItemTemplate().isCanFuse() || !secondItem.getItemTemplate().isCanFuse()) {
            PacketSendUtility.sendMessage(player, "You performed illegal operation, admin will catch you");
            log.info("[AUDIT] Client hack with item fusion, player: " + player.getName());
            return;
        } if (!firstItem.getItemTemplate().isTwoHandWeapon()) {
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_COMPOUND_ERROR_NOT_AVAILABLE(firstItem.getNameId()));
            return;
        } if (firstItem.getItemTemplate().getWeaponType() != secondItem.getItemTemplate().getWeaponType()) {
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_COMPOUND_ERROR_DIFFERENT_TYPE);
            return;
        } if (secondItem.getItemTemplate().getLevel() > firstItem.getItemTemplate().getLevel()) {
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_COMPOUND_ERROR_MAIN_REQUIRE_HIGHER_LEVEL);
            return;
        } if (firstItem.getImprovement() != null && secondItem.getImprovement() != null) {
            if (firstItem.getImprovement().getChargeWay() != secondItem.getImprovement().getChargeWay()) {
                PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_COMPOUND_ERROR_NOT_COMPARABLE_ITEM);
                return;
            }
        }
        firstItem.setFusionedItem(secondItem.getItemTemplate());
        ItemSocketService.removeAllFusionStone(player, firstItem);
        if (secondItem.hasOptionalSocket()) {
            firstItem.setOptionalFusionSocket(secondItem.getOptionalSocket());
		} else {
			firstItem.setOptionalFusionSocket(0);
		}
		ItemSocketService.copyFusionStones(secondItem, firstItem);
        firstItem.setPersistentState(PersistentState.UPDATE_REQUIRED);
        InventoryDAO.store(firstItem, player);
        if (!player.getInventory().decreaseByObjectId(secondItemUniqueId, 1))
            return;
        ItemPacketService.updateItemAfterInfoChange(player, firstItem);
        player.getInventory().decreaseKinah(price);
        PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_COMPOUND_SUCCESS(firstItem.getNameId(), secondItem.getNameId()));
    }

	private static double rarityRate(ItemQuality rarity) {
		switch (rarity) {
			case RARE:
				return 1.0;
			case LEGEND:
				return 1.5;
			case UNIQUE:
				return 2.0;
			case EPIC:
				return 2.5;
			default:
				return 1.0;
		}
	}

	public static void breakWeapons(Player player, int weaponToBreakUniqueId) {
		Item weaponToBreak = player.getInventory().getItemByObjId(weaponToBreakUniqueId);
		if (weaponToBreak == null) {
			weaponToBreak = player.getEquipment().getEquippedItemByObjId(weaponToBreakUniqueId);
		} if (!weaponToBreak.hasFusionedItem()) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_DECOMPOUND_ERROR_NOT_AVAILABLE(weaponToBreak.getNameId()));
			return;
		}
		weaponToBreak.setFusionedItem(null);
		ItemSocketService.removeAllFusionStone(player, weaponToBreak);
		InventoryDAO.store(weaponToBreak, player);
		ItemPacketService.updateItemAfterInfoChange(player, weaponToBreak);
		PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_COMPOUNDED_ITEM_DECOMPOUND_SUCCESS(weaponToBreak.getNameId()));
	}
}
