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
package com.aionemu.gameserver.services;

import com.aionemu.commons.utils.Rnd;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.model.items.ManaStone;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatEnchantFunction;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.stats.listeners.ItemEquipmentListener;
import com.aionemu.gameserver.model.templates.item.*;
import com.aionemu.gameserver.model.templates.item.actions.EnchantItemAction;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.item.ItemPacketService;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.item.ItemSocketService;
import com.aionemu.gameserver.services.player.BattlePassService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.RndArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class EnchantService
{
	private static final Logger log = LoggerFactory.getLogger(EnchantService.class);
	
	public static boolean equipExtraction(Player player, Item targetItem) {
		Storage inventory = player.getInventory();
		if (inventory.getItemByObjId(targetItem.getObjectId()) == null) {
            return false;
        }
		ItemService.addItem(player, RndArray.get(enchantStone), Rnd.get(1, 2));
		return true;
	}
	
	private static final int[] enchantStone = {
		166000001, 166000002, 166000003, 166000004, 166000005, 166000006, 166000007, 166000008, 166000009,
		166000010, 166000011, 166000012, 166000013, 166000014, 166000015, 166000016, 166000017, 166000018, 166000019,
		166000020, 166000021, 166000022, 166000023, 166000024, 166000025, 166000026, 166000027, 166000028, 166000029,
		166000030, 166000031, 166000032, 166000033, 166000034, 166000035, 166000036, 166000037, 166000038, 166000039,
		166000040, 166000041, 166000042, 166000043, 166000044, 166000045, 166000046, 166000047, 166000048, 166000049,
		166000050, 166000051, 166000052, 166000053, 166000054, 166000055, 166000056, 166000057, 166000058, 166000059,
		166000060, 166000061, 166000062, 166000063, 166000064, 166000065, 166000066, 166000067, 166000068, 166000069,
		166000070, 166000071, 166000072, 166000073, 166000074, 166000075, 166000076, 166000077, 166000078, 166000079,
		166000080, 166000081, 166000082, 166000083, 166000084, 166000085, 166000086, 166000087, 166000088, 166000089,
		166000090, 166000091, 166000092, 166000093, 166000094, 166000095, 166000096, 166000097, 166000098, 166000099
	};
	
	public static boolean enchantItem(Player player, Item parentItem, Item targetItem, Item supplementItem) {
		ItemTemplate enchantStone = parentItem.getItemTemplate();
		int enchantStoneLevel = enchantStone.getLevel();
		int targetItemLevel = targetItem.getItemTemplate().getLevel();
		int enchantItemLevel = targetItem.getEnchantLevel() + 1;
		int qualityCap = 0;
		float success = 60;
		int levelDiff = enchantStoneLevel - targetItemLevel;
		success += levelDiff > 0 ? levelDiff * 3f / qualityCap : 0;
		success += levelDiff - qualityCap;
		success -= targetItem.getEnchantLevel() * qualityCap / (enchantItemLevel > 10 ? 5f : 6f);
		if (supplementItem != null) {
			float addSuccessRate = 0;
			int supplementUseCount = 1;
			ItemTemplate supplementTemplate = supplementItem.getItemTemplate();
			EnchantItemAction action = supplementTemplate.getActions().getEnchantAction();
			if (action != null) {
				if (action.isManastoneOnly()) {
					return false;
				}
				addSuccessRate = action.getChance() * 2;
			}
			action = enchantStone.getActions().getEnchantAction();
			if (action != null) {
				supplementUseCount = action.getCount();
			} if (player.getInventory().getItemCountByItemId(supplementTemplate.getTemplateId()) < supplementUseCount) {
				return false;
			}
			success += addSuccessRate;
			player.getInventory().decreaseByItemId(supplementUseCount, supplementTemplate.getTemplateId());
		} if (success >= 95) {
			success = 95;
		}
		boolean result = false;
		float random = Rnd.get(1, 1000) / 10f;
		if (random <= success) {
			result = true;
		}
		return result;
	}
	
   /**
	* Enchant.
	*/
	public static void enchantItemAct(Player player, Item parentItem, Item targetItem, Item supplementItem, int currentEnchant, boolean result) {
		int rnd = Rnd.get(100);
		currentEnchant = targetItem.getEnchantLevel();
		ItemQuality targetQuality = targetItem.getItemTemplate().getItemQuality();
		if (!player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1)) {
			return;
		}
		player.updateSupplements(result);
		if (result) {
            switch (targetQuality) {
				case COMMON:
				case RARE:
				case LEGEND:
				case UNIQUE:
				case EPIC:
                    int chanceId = getChanceId(targetItem);
                    ItemEnchantChance eItem = DataManager.ITEM_ENCHANT_CHANCES_DATA.getChanceById(chanceId);
                    ItemEnchantChanceList eData = eItem.getChancesById(targetItem.getEnchantLevel());
					if (rnd <= eData.getCrit()) {
                        currentEnchant += Rnd.get(2, 3);
                    } else {
                        currentEnchant += 1;
                    }
                break;
            }
        } else {
            if (currentEnchant > 0) {
                currentEnchant -= 1;
            } else if (currentEnchant == 0) {
                currentEnchant = 0;
            }
        }
		targetItem.setEnchantLevel(currentEnchant);
		BattlePassService.getInstance().OnEnchant(player , targetItem.getEnchantLevel());
		if (targetItem.isEquipped()) {
			player.getGameStats().updateStatsVisually();
			player.getController().updatePassiveStats();
			player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
		} else {
            player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);
        }
		ItemPacketService.updateItemAfterInfoChange(player, targetItem, ItemUpdateType.STATS_CHANGE);
		if (result) {
			///You successfully enchanted %0.
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_ENCHANT_ITEM_SUCCEED(new DescriptionId(targetItem.getNameId())));
		} else {
			///You have failed to enchant %0.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_ENCHANT_ITEM_FAILED(new DescriptionId(targetItem.getNameId())));
		}
	}
	
	public static boolean enchantItem(Player player, Item parentItem, Item targetItem) {
        boolean result = false;
        float random = Rnd.get(1, 1000) / 10f;
        switch (targetItem.getItemTemplate().getItemQuality()) {
			case COMMON:
			case RARE:
			case LEGEND:
			case UNIQUE:
			case EPIC:
                int chanceId = getChanceId(targetItem);
                ItemEnchantChance eItem = DataManager.ITEM_ENCHANT_CHANCES_DATA.getChanceById(chanceId);
                ItemEnchantChanceList eData = eItem.getChancesById(targetItem.getEnchantLevel());
                if (player.isGM() && AdminConfig.GM_ENCHANT_NOFAIL) {
                   result = true;
                } else {
                    if (random <= eData.getChance()) {
                        result = true;
                    } else {
                        result = false;
                    }
                }
            break;
        }
        return result;
    }
	
    private static int getChanceId(Item targetItem) {
		switch (targetItem.getItemTemplate().getItemQuality()) {
			case COMMON:
			case RARE:
				return 1;
			case LEGEND:
			    return 2;
			case UNIQUE:
				return 3;
			case EPIC:
				return 4;
		}
        return 0;
    }
	
	public static boolean socketManastone(Player player, Item parentItem, Item targetItem, Item supplementItem, int targetWeapon) {
		int targetItemLevel = 1;
		if (targetWeapon == 1) {
			targetItemLevel = targetItem.getItemTemplate().getLevel();
		} else {
			targetItemLevel = targetItem.getFusionedItemTemplate().getLevel();
		}
		int stoneLevel = parentItem.getItemTemplate().getLevel();
		int slotLevel = (int) (10 * Math.ceil((targetItemLevel + 10) / 10d));
		boolean result = false;
		float success = 30;
		int stoneCount = 0;
		if (stoneLevel > slotLevel) {
			return false;
		} if (targetWeapon == 1) {
			stoneCount = targetItem.getItemStones().size();
		} else {
			stoneCount = targetItem.getFusionStones().size();
		}
		success += parentItem.getItemTemplate().getItemQuality() == ItemQuality.RARE ? 25f : 15f;
		float socketDiff = stoneCount * 1.25f + 1.75f;
		success += (slotLevel - stoneLevel) / socketDiff;
		if (supplementItem != null) {
			int supplementUseCount = 1;
			ItemTemplate manastoneTemplate = parentItem.getItemTemplate();
			int manastoneCount;
			if (targetWeapon == 1) {
				manastoneCount = targetItem.getItemStones().size() + 1;
			} else {
				manastoneCount = targetItem.getFusionStones().size() + 1;
			}
			ItemTemplate supplementTemplate = supplementItem.getItemTemplate();
			float addSuccessRate = 0;			
			boolean isManastoneOnly = false;
			EnchantItemAction action = manastoneTemplate.getActions().getEnchantAction();
			if (action != null) {
				supplementUseCount = action.getCount();
			}
			action = supplementTemplate.getActions().getEnchantAction();
			if (action != null) {
				addSuccessRate = action.getChance();
				isManastoneOnly = action.isManastoneOnly();
			} switch (parentItem.getItemTemplate().getItemQuality()) {
				case COMMON:
				case RARE:
				    addSuccessRate *= 70;
				break;
				case LEGEND:
				    addSuccessRate *= 65;
				break;
			    case UNIQUE:
				    addSuccessRate *= 60;
				break;
			    case EPIC:
				    addSuccessRate *= 55;
				break;
			} if (isManastoneOnly) {
				supplementUseCount = 1;
			} else if (stoneCount > 0) {
				supplementUseCount = supplementUseCount * manastoneCount;
			} if (player.getInventory().getItemCountByItemId(supplementTemplate.getTemplateId()) < supplementUseCount) {
				return false;
			}
			success += addSuccessRate;
			player.getInventory().decreaseByItemId(supplementUseCount, supplementTemplate.getTemplateId());
		}
		float random = Rnd.get(1, 1000) / 10f;
		if (random <= success) {
			result = true;
		}
		return result;
	}
	
	public static void socketManastoneAct(Player player, Item parentItem, Item targetItem, Item supplementItem, int targetWeapon, boolean result) {
		player.updateSupplements(result);
		if (player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1) && result) {
			///You have successfully socketed a manastone in %0.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_GIVE_ITEM_OPTION_SUCCEED(new DescriptionId(targetItem.getNameId())));
			if (targetWeapon == 1) {
				ManaStone manaStone = ItemSocketService.addManaStone(targetItem, parentItem.getItemTemplate().getTemplateId());
				if (targetItem.isEquipped()) {
					ItemEquipmentListener.addStoneStats(targetItem, manaStone, player.getGameStats());
					player.getGameStats().updateStatsAndSpeedVisually();
				}
			} else {
				ManaStone manaStone = ItemSocketService.addFusionStone(targetItem, parentItem.getItemTemplate().getTemplateId());
				if (targetItem.isEquipped()) {
					ItemEquipmentListener.addStoneStats(targetItem, manaStone, player.getGameStats());
					player.getGameStats().updateStatsAndSpeedVisually();
				}
				ItemPacketService.updateItemAfterInfoChange(player, targetItem, ItemUpdateType.STATS_CHANGE);
				if (targetItem.isEquipped()) {
					player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
				} else {
					player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);
				}
			}
		} else {
			///You have failed in the manastone socketing of %0.
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_GIVE_ITEM_OPTION_FAILED(new DescriptionId(targetItem.getNameId())));
		}
		ItemPacketService.updateItemAfterInfoChange(player, targetItem);
	}
	
	public static void onItemEquip(Player player, Item item) {
		List<IStatFunction> modifiers = new ArrayList<IStatFunction>();
		try {
			if (item.getItemTemplate().isWeapon()) {
				switch (item.getItemTemplate().getWeaponType()) {
				    case ORB_2H:
				    case BOOK_2H:
				    	modifiers.add(new StatEnchantFunction(item, StatEnum.BOOST_MAGICAL_SKILL));
				    	modifiers.add(new StatEnchantFunction(item, StatEnum.MAGICAL_ATTACK));
				    break;
				    case MACE_1H:
				    case STAFF_2H:
				    	modifiers.add(new StatEnchantFunction(item, StatEnum.MAGICAL_ATTACK));
				    	modifiers.add(new StatEnchantFunction(item, StatEnum.PHYSICAL_ATTACK));
				    	modifiers.add(new StatEnchantFunction(item, StatEnum.BOOST_MAGICAL_SKILL));
				    break;
				    case SWORD_1H:
				    case DAGGER_1H:
				    	if (item.getEquipmentSlot() == ItemSlot.MAIN_HAND.getSlotIdMask()) {
				    		modifiers.add(new StatEnchantFunction(item, StatEnum.MAIN_HAND_POWER));
				    	} else {
				    		modifiers.add(new StatEnchantFunction(item, StatEnum.OFF_HAND_POWER));
				    	}
					break;
				    case SWORD_2H:
				    case BOW:
				    case POLEARM_2H:
					case BLADE_2H:
				    	modifiers.add(new StatEnchantFunction(item, StatEnum.PHYSICAL_ATTACK));
				    break;
				}
			} else if (item.getItemTemplate().isArmor()) {
				if (item.getItemTemplate().getArmorType() == ArmorType.SHIELD) {
					modifiers.add(new StatEnchantFunction(item, StatEnum.DAMAGE_REDUCE));
					modifiers.add(new StatEnchantFunction(item, StatEnum.BLOCK));
				} if (item.getItemTemplate().getArmorType() != ArmorType.SHIELD && item.getItemTemplate().getItemSlot() != 32768 && !item.getItemTemplate().isAccessory()) {
					modifiers.add(new StatEnchantFunction(item, StatEnum.PHYSICAL_DEFENSE));
					modifiers.add(new StatEnchantFunction(item, StatEnum.MAGICAL_DEFEND));
					modifiers.add(new StatEnchantFunction(item, StatEnum.MAXHP));
					modifiers.add(new StatEnchantFunction(item, StatEnum.PHYSICAL_CRITICAL_RESIST));
					modifiers.add(new StatEnchantFunction(item, StatEnum.PHYSICAL_ATTACK));
					modifiers.add(new StatEnchantFunction(item, StatEnum.BOOST_MAGICAL_SKILL));
				}
			} if (!modifiers.isEmpty()) {
				player.getGameStats().addEffect(item, modifiers);
				player.getGameStats().updateStatsAndSpeedVisually();
			}
		} catch (Exception ex) {
			log.error("Error on item equip.", ex);
		}
	}
}