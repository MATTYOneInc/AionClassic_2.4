/*
 * This file is part of aion-unique <aion-unique.com>.
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
package com.aionemu.gameserver.model.stats.listeners;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.*;
import com.aionemu.gameserver.model.stats.calc.functions.StatAddFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatFunction;
import com.aionemu.gameserver.model.stats.container.CreatureGameStats;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.WeaponStats;
import com.aionemu.gameserver.model.templates.item.WeaponType;
import com.aionemu.gameserver.model.templates.itemset.FullBonus;
import com.aionemu.gameserver.model.templates.itemset.ItemSetTemplate;
import com.aionemu.gameserver.model.templates.itemset.PartBonus;
import com.aionemu.gameserver.services.EnchantService;
import com.aionemu.gameserver.services.item.ItemChargeService;
import com.aionemu.gameserver.utils.PacketSendUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author xavier modified by Wakizashi
 */
public class ItemEquipmentListener {

	static Logger log = LoggerFactory.getLogger(ItemEquipmentListener.class);

	/**
	 * @param item
	 * @param cgs
	 */
	public static void onItemEquipment(Item item, Player owner) {
		owner.getController().cancelUseItem();
		ItemTemplate itemTemplate = item.getItemTemplate();
		onItemEquipment(item, owner.getGameStats());
		if (itemTemplate.isItemSet()) {
			recalculateItemSet(itemTemplate.getItemSet(), owner, item.getItemTemplate().isWeapon());
		} if (item.hasManaStones()) {
			addStonesStats(item, item.getItemStones(), owner.getGameStats());
		} if (item.hasFusionStones()) {
			addStonesStats(item, item.getFusionStones(), owner.getGameStats());
		} if (item.getConditioningInfo() != null) {
			owner.getObserveController().addObserver(item.getConditioningInfo());
			item.getConditioningInfo().setPlayer(owner);
		}
		addGodstoneEffect(owner, item);
		if (owner.isProtectionActive()) {
			owner.getController().stopProtectionActiveTask();
		}
		EnchantService.onItemEquip(owner, item);
		ItemChargeService.onItemEquip(owner, item);
	}
	
	public static void onItemUnequipment(Item item, Player owner) {
		owner.getController().cancelUseItem();
		ItemTemplate itemTemplate = item.getItemTemplate();
		if (itemTemplate.isItemSet()) {
			recalculateItemSet(itemTemplate.getItemSet(), owner, item.getItemTemplate().isWeapon());
		}
		owner.getGameStats().endEffect(item);
		if (item.hasManaStones()) {
			removeStoneStats(item.getItemStones(), owner.getGameStats());
		} if (item.hasFusionStones()) {
			removeStoneStats(item.getFusionStones(), owner.getGameStats());
		} if (item.getConditioningInfo() != null) {
			owner.getObserveController().removeObserver(item.getConditioningInfo());
			item.getConditioningInfo().setPlayer(null);
		}
		removeGodstoneEffect(owner, item);
		if (owner.isProtectionActive()) {
			owner.getController().stopProtectionActiveTask();
		}
	}

	/**
	 * @param itemTemplate
	 * @param slot
	 * @param cgs
	 */
	private static void onItemEquipment(Item item, CreatureGameStats<?> cgs) {
		ItemTemplate itemTemplate = item.getItemTemplate();
		long slot = item.getEquipmentSlot();
		List<StatFunction> allModifiers = new ArrayList<StatFunction>();
		if (itemTemplate.getModifiers() == null) {
			return;
		}
		allModifiers.addAll(itemTemplate.getModifiers());
		if ((slot & ItemSlot.MAIN_OR_SUB.getSlotIdMask()) != 0) {
			allModifiers = wrapModifiers(item, allModifiers);
			if (item.hasFusionedItem()) {
				ItemTemplate fusionedItemTemplate = item.getFusionedItemTemplate();
				WeaponType weaponType = fusionedItemTemplate.getWeaponType();
				List<StatFunction> fusionedItemModifiers = fusionedItemTemplate.getModifiers();
				if (fusionedItemModifiers != null) {
					allModifiers.addAll(wrapModifiers(item, fusionedItemModifiers));
				}
				WeaponStats weaponStats = fusionedItemTemplate.getWeaponStats();
				if (weaponStats != null) {
					int boostMagicalSkill = Math.round(0.1f * (float)weaponStats.getBoostMagicalSkill());
					int attack = Math.round(0.1f * (float)weaponStats.getMeanDamage());
					if (weaponType == WeaponType.ORB_2H || weaponType == WeaponType.STAFF_2H || weaponType == WeaponType.BOOK_2H) {			    
						allModifiers.add(new StatAddFunction(StatEnum.BOOST_MAGICAL_SKILL, boostMagicalSkill, false));
					}
					allModifiers.add(new StatAddFunction(item.getItemTemplate().getAttackType().isMagical() ? StatEnum.MAGICAL_ATTACK : StatEnum.PHYSICAL_ATTACK, attack, false));
				}
			}
		}
		item.setCurrentModifiers(allModifiers);
		cgs.addEffect(item, allModifiers);
	}

	/**
	 * Filter stats based on the following rules:<br>
	 * 1) don't include fusioned stats which will be taken only from 1 weapon <br>
	 * 2) wrap stats which are different for MAIN and OFF hands<br>
	 * 3) add the rest<br>
	 * 
	 * @param item
	 * @param modifiers
	 * @return
	 */
	private static List<StatFunction> wrapModifiers(Item item, List<StatFunction> modifiers) {
		List<StatFunction> allModifiers = new ArrayList<StatFunction>();
		for (StatFunction modifier : modifiers) {
			switch (modifier.getName()) {
				case ATTACK_SPEED:
				case PVP_ATTACK_RATIO:
				case PVP_DEFEND_RATIO:
				case BOOST_CASTING_TIME:
				continue;
				default:
				allModifiers.add(modifier);
			}
		}
		return allModifiers;
	}

	/**
	 * @param itemSetTemplate
	 * @param player
	 * @param isWeapon
	 */
	private static void recalculateItemSet(ItemSetTemplate itemSetTemplate, Player player, boolean isWeapon) {
		if (itemSetTemplate == null) {
			return;
		}
		int itemSetPartsEquipped = player.getEquipment().itemSetPartsEquipped(itemSetTemplate.getId());
		if (itemSetTemplate.getFullbonus() != null) {
			if (itemSetPartsEquipped > itemSetTemplate.getFullbonus().getCount()) {
				return;
			}
		}
		player.getGameStats().endEffect(itemSetTemplate);
		int mainHandItemId = 0;
		int offHandItemId = 0;
		if (player.getEquipment().getMainHandWeapon() != null) {
			mainHandItemId = player.getEquipment().getMainHandWeapon().getItemId();
		} if (player.getEquipment().getOffHandWeapon() != null) {
			offHandItemId = player.getEquipment().getOffHandWeapon().getItemId();
		}
		boolean mainAndOffNotSame = mainHandItemId != offHandItemId;
		for (PartBonus itempartbonus : itemSetTemplate.getPartbonus()) {
			if (mainAndOffNotSame && isWeapon) {
				if (itempartbonus.getCount() <= itemSetPartsEquipped) {
					player.getGameStats().addEffect(itemSetTemplate, itempartbonus.getModifiers());
				}
			} else if (!isWeapon) {
				if (itempartbonus.getCount() <= itemSetPartsEquipped) {
					if (itempartbonus.getModifiers() != null) {
						player.getGameStats().addEffect(itemSetTemplate, itempartbonus.getModifiers());
					}
				}
			}
		}
		FullBonus fullbonus = itemSetTemplate.getFullbonus();
		if (fullbonus != null && itemSetPartsEquipped == fullbonus.getCount()) {
			player.getGameStats().addEffect(itemSetTemplate, fullbonus.getModifiers());
		}
	}

	/**
	 * All modifiers of stones will be applied to character
	 * 
	 * @param item
	 * @param cgs
	 */
	private static void addStonesStats(Item item, Set<? extends ManaStone> itemStones, CreatureGameStats<?> cgs) {
		if (itemStones == null || itemStones.size() == 0)
			return;

		for (ManaStone stone : itemStones) {
			addStoneStats(item, stone, cgs);
		}
	}

	/**
	 * Used when socketing of equipped item
	 * 
	 * @param item
	 * @param stone
	 * @param cgs
	 */
	public static void addStoneStats(Item item, ManaStone stone, CreatureGameStats<?> cgs) {
		List<StatFunction> modifiers = stone.getModifiers();
		if (modifiers == null) {
			return;
		}
		cgs.addEffect(stone, modifiers);
	}

	/**
	 * All modifiers of stones will be removed
	 * 
	 * @param itemStones
	 * @param cgs
	 */
	public static void removeStoneStats(Set<? extends ManaStone> itemStones, CreatureGameStats<?> cgs) {
		if (itemStones == null || itemStones.size() == 0)
			return;

		for (ManaStone stone : itemStones) {
			List<StatFunction> modifiers = stone.getModifiers();
			if (modifiers != null) {
				cgs.endEffect(stone);
			}
		}
	}

	/**
	 * @param item
	 */
	private static void addGodstoneEffect(Player player, Item item) {
		if (item.getGodStone() != null) {
			item.getGodStone().onEquip(player);
		}
	}

	/**
	 * @param item
	 */
	private static void removeGodstoneEffect(Player player, Item item) {
		if (item.getGodStone() != null) {
			item.getGodStone().onUnEquip(player);
		}
	}

	public static void UpdateEquipmentItem(Player player, Item item) {
        if (!item.isEquipped()) {
            return;
        }
        player.getGameStats().endEffect(item);
        ItemEquipmentListener.onItemEquipment(item, player.getGameStats());
    }
}