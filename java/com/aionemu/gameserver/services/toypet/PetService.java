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
package com.aionemu.gameserver.services.toypet;

import com.aionemu.gameserver.dao.PlayerPetsDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.model.gameobjects.player.PetCommonData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.common.legacy.LootRuleType;
import com.aionemu.gameserver.model.templates.item.ItemUseLimits;
import com.aionemu.gameserver.model.templates.item.actions.AbstractItemAction;
import com.aionemu.gameserver.model.templates.item.actions.ItemActions;
import com.aionemu.gameserver.model.templates.pet.*;
import com.aionemu.gameserver.network.aion.serverpackets.S_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.S_FUNCTIONAL_PET;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.item.ItemPacketService.ItemUpdateType;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

public class PetService
{
	Logger log = LoggerFactory.getLogger(PetService.class);

	public static final PetService getInstance() {
		return SingletonHolder.instance;
	}

	private PetService() {
	}

	public void renamePet(Player player, String name) {
		Pet pet = player.getPet();
		if (pet != null) {
			pet.getCommonData().setName(name);
			PlayerPetsDAO.updatePetName(pet.getCommonData());
			PacketSendUtility.broadcastPacket(player, new S_FUNCTIONAL_PET(10, pet), true);
		}
	}

	public void onPlayerLogin(Player player) {
		Collection<PetCommonData> playerPets = player.getPetList().getPets();
		if (playerPets != null && playerPets.size() > 0) {
			PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(0, playerPets));
		}
	}

	public void removeObject(int objectId, int count, int action, Player player) {
		Item item = player.getInventory().getItemByObjId(objectId);
		if (item == null || player.getPet() == null || count > item.getItemCount()) {
			return;
		}
		Pet pet = player.getPet();
		pet.getCommonData().setCancelFeed(false);
		PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(1, action, item.getObjectId(), count, pet));
		PacketSendUtility.sendPacket(player, new S_ACTION(player, EmotionType.START_FEEDING, 0, player.getObjectId()));
		schedule(pet, player, item, count, action);
	}

	private void schedule(final Pet pet, final Player player, final Item item, final int count, final int action) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (!pet.getCommonData().getCancelFeed()) {
					checkFeeding(pet, player, item, count, action);
				}
			}
		}, 2500);
	}

	private void checkFeeding(Pet pet, Player player, Item item, int count, int action) {
		PetCommonData commonData = pet.getCommonData();
		PetFeedProgress progress = commonData.getFeedProgress();
		if (!commonData.getCancelFeed()) {
			PetFunction func = pet.getPetTemplate().getPetFunction(PetFunctionType.FOOD);
			PetFlavour flavour = DataManager.PET_FEED_DATA.getFlavourById(func.getId());
			FoodType foodType = flavour.getFoodType(item.getItemId());
			PetFeedResult reward = null;
			if (flavour.isLovedFood(foodType, item.getItemId()) && progress.getLovedFoodRemaining() == 0) {
				foodType = null;
			} if (foodType != null) {
				player.getInventory().decreaseItemCount(item, 1, ItemUpdateType.DEC_PET_FOOD);
				reward = flavour.processFeedResult(progress, foodType, item.getItemTemplate().getLevel(), player.getCommonData().getLevel());
				if (progress.getHungryLevel() == PetHungryLevel.FULL && reward != null) {
					PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(2, action, item.getObjectId(), 0, pet));
				} else {
					PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(2, action, item.getObjectId(), --count, pet));
				}
			} else {
				PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(5, action, 0, 0, pet));
				PacketSendUtility.sendPacket(player, new S_ACTION(player, EmotionType.END_FEEDING, 0, player.getObjectId()));
				PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_TOYPET_FEED_FOOD_NOT_LOVEFLAVOR(pet.getName(), item.getItemTemplate().getNameId()));
				return;
			} if (progress.getHungryLevel() == PetHungryLevel.FULL && reward != null) {
				PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(6, action, reward.getItem(), 0, pet));
				PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(5, action, 0, 0, pet));
				PacketSendUtility.sendPacket(player, new S_ACTION(player, EmotionType.END_FEEDING, 0, player.getObjectId()));
				PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(7, action, 0, 0, pet));
				ItemService.addItem(player, reward.getItem(), 1);
				commonData.setReFoodTime(flavour.getCooldDown() * 60000);
				commonData.setCurentTime(System.currentTimeMillis());
				PlayerPetsDAO.setTime(player, pet.getPetId(), System.currentTimeMillis());
				progress.reset();
			} else if (count > 0) {
				schedule(pet, player, item, count, action);
			} else {
				PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(5, action, 0, 0, pet));
				PacketSendUtility.sendPacket(player, new S_ACTION(player, EmotionType.END_FEEDING, 0, player.getObjectId()));
			}
		}
	}

	public void relocateDoping(Player player, int targetSlot, int destinationSlot) {
		Pet pet = player.getPet();
		if (pet == null || pet.getCommonData().getDopingBag() == null) {
			return;
		}
		int[] scrollBag = pet.getCommonData().getDopingBag().getScrollsUsed();
		int targetItem = scrollBag[targetSlot - 2];
		if (destinationSlot - 2 > scrollBag.length - 1) {
			pet.getCommonData().getDopingBag().setItem(targetItem, destinationSlot);
			PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(0, targetItem, destinationSlot));
			pet.getCommonData().getDopingBag().setItem(0, targetSlot);
			PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(0, 0, targetSlot));
		} else {
			pet.getCommonData().getDopingBag().setItem(scrollBag[destinationSlot - 2], targetSlot);
			PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(0, scrollBag[destinationSlot - 2], targetSlot));
			pet.getCommonData().getDopingBag().setItem(targetItem, destinationSlot);
			PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(0, targetItem, destinationSlot));
		}
	}

	public void useDoping(final Player player, int action, int itemId, int slot) {
		Pet pet = player.getPet();
		if (pet == null || pet.getCommonData().getDopingBag() == null) {
			return;
		} if (action < 2) {
			pet.getCommonData().getDopingBag().setItem(itemId, slot);
			action = 0;
		} else if (action == 3) {
			List<Item> items = player.getInventory().getItemsByItemId(itemId);
			for (;;) {
				Item useItem = items.get(0);
				ItemActions itemActions = useItem.getItemTemplate().getActions();
				ItemUseLimits limit = new ItemUseLimits();
				int useDelay = player.getItemCooldown(useItem.getItemTemplate()) / 3;
				if (useDelay < 3000) {
					useDelay = 3000;
				}
				limit.setDelayId(useItem.getItemTemplate().getUseLimits().getDelayId());
				limit.setDelayTime(useDelay);
				if (player.isItemUseDisabled(limit)) {
					final int useAction = action;
					final int useItemId = itemId;
					final int useSlot = slot;
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(useAction, useItemId, useSlot));
						}
					}, useDelay);
					return;
				} if (!RestrictionsManager.canUseItem(player, useItem) || player.isProtectionActive()) {
					player.addItemCoolDown(limit.getDelayId(), System.currentTimeMillis() + useDelay, useDelay / 1000);
					break;
				}
				player.getController().cancelCurrentSkill();
				for (AbstractItemAction itemAction : itemActions.getItemActions()) {
					if (itemAction.canAct(player, useItem, null)) {
						itemAction.act(player, useItem, null);
					}
				}
				break;
			}
		}
		PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(action, itemId, slot));
		itemId = pet.getCommonData().getDopingBag().getFoodItem();
		long totalDopes = player.getInventory().getItemCountByItemId(itemId);
		itemId = pet.getCommonData().getDopingBag().getDrinkItem();
		totalDopes += player.getInventory().getItemCountByItemId(itemId);
		int[] scrollBag = pet.getCommonData().getDopingBag().getScrollsUsed();
		for (int i = 0; i < scrollBag.length; i++) {
			if (scrollBag[i] != 0) {
				totalDopes += player.getInventory().getItemCountByItemId(scrollBag[i]);
			}
		} if (totalDopes == 0) {
			pet.getCommonData().setIsBuffing(false);
			PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(1, false));
		}
	}

	public void activateLoot(final Player player, final boolean activate) {
		if (player.getPet() == null) {
			return;
		} if (activate) {
			if (player.isInTeam()) {
				LootRuleType lootType = player.getLootGroupRules().getLootRule();
				if (lootType == LootRuleType.FREEFORALL) {
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_LOOTING_PET_MESSAGE03);
					return;
				}
			}
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_LOOTING_PET_MESSAGE01);
		}
		player.getPet().getCommonData().setIsLooting(activate);
		PacketSendUtility.sendPacket(player, new S_FUNCTIONAL_PET(activate));
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		protected static final PetService instance = new PetService();
	}
}
