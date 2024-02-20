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
package com.aionemu.gameserver.services.player;

import com.aionemu.commons.utils.GenericValidator;
import com.aionemu.gameserver.configs.main.CacheConfig;
import com.aionemu.gameserver.controllers.FlyController;
import com.aionemu.gameserver.controllers.PlayerController;
import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.dao.*;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.PlayerInitialData;
import com.aionemu.gameserver.dataholders.PlayerInitialData.LocationData;
import com.aionemu.gameserver.dataholders.PlayerInitialData.PlayerCreationData;
import com.aionemu.gameserver.dataholders.PlayerInitialData.PlayerCreationData.ItemType;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.player.*;
import com.aionemu.gameserver.model.items.ItemSlot;
import com.aionemu.gameserver.model.items.storage.PlayerStorage;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.skill.PlayerSkillList;
import com.aionemu.gameserver.model.stats.calc.functions.PlayerStatFunctions;
import com.aionemu.gameserver.model.stats.listeners.TitleChangeListener;
import com.aionemu.gameserver.model.team.legion.LegionMember;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.services.PunishmentService.PunishmentType;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.services.item.ItemFactory;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.legion.LegionService;
import com.aionemu.gameserver.utils.collections.cachemap.CacheMap;
import com.aionemu.gameserver.utils.collections.cachemap.CacheMapFactory;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldPosition;
import com.aionemu.gameserver.world.knownlist.KnownList;
import com.aionemu.gameserver.world.knownlist.Visitor;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.sql.Timestamp;
import java.util.*;

public class PlayerService
{
	private static final CacheMap<Integer, Player> playerCache = CacheMapFactory.createSoftCacheMap("Player", "player");

	public static boolean isFreeName(String name) {
		return !PlayerDAO.isNameUsed(name);
	}

	public static boolean isOldName(String name) {
		return OldNamesDAO.isOldName(name);
	}

	public static boolean storeNewPlayer(Player player, String accountName, int accountId) {
		return PlayerDAO.saveNewPlayer(player.getCommonData(), accountId, accountName)
		&& PlayerAppearanceDAO.store(player)
		&& PlayerSkillListDAO.storeSkills(player)
		&& InventoryDAO.store(player);
	}

	public static void storePlayer(Player player) {
		PlayerDAO.storePlayer(player);
		PlayerSkillListDAO.storeSkills(player);
		PlayerSettingsDAO.saveSettings(player);
		PlayerQuestListDAO.store(player);
		AbyssRankDAO.storeAbyssRank(player);
		PlayerPunishmentsDAO.storePlayerPunishments(player, PunishmentType.PRISON);
		PlayerPunishmentsDAO.storePlayerPunishments(player, PunishmentType.GATHER);
		InventoryDAO.store(player);
		ItemStoneListDAO.save(player);
		MailDAO.storeMailbox(player);
		PortalCooldownsDAO.storePortalCooldowns(player);
		CraftCooldownsDAO.storeCraftCooldowns(player);
		PlayerNpcFactionsDAO.storeNpcFactions(player);
		EventItemsDAO.loadItems(player);
	}

	public static Player getPlayer(int playerObjId, Account account) {
		Player player = playerCache.get(playerObjId);
		if (player != null) {
			return player;
		}
		PlayerAccountData playerAccountData = account.getPlayerAccountData(playerObjId);
		PlayerCommonData pcd = playerAccountData.getPlayerCommonData();
		PlayerAppearance appearance = playerAccountData.getAppereance();
		player = new Player(new PlayerController(), pcd, appearance, account);
		LegionMember legionMember = LegionService.getInstance().getLegionMember(player.getObjectId());
		if (legionMember != null) {
			player.setLegionMember(legionMember);
		}
		MacroList macroses = PlayerMacrossesDAO.restoreMacrosses(playerObjId);
		player.setMacroList(macroses);
		player.setSkillList(PlayerSkillListDAO.loadSkillList(playerObjId));
		player.setKnownlist(new KnownList(player));
		player.setFriendList(FriendListDAO.load(player));
		player.setBlockList(BlockListDAO.load(player));
		player.setTitleList(PlayerTitleListDAO.loadTitleList(playerObjId));
		PlayerSettingsDAO.loadSettings(player);
		AbyssRankDAO.loadAbyssRank(player);
		PlayerNpcFactionsDAO.loadNpcFactions(player);
		MotionDAO.loadMotionList(player);
		player.setVars(PlayerVarsDAO.load(player.getObjectId()));
		Equipment equipment = InventoryDAO.loadEquipment(player);
		ItemService.loadItemStones(equipment.getEquippedItemsWithoutStigma());
		equipment.setOwner(player);
		player.setEquipment(equipment);
		player.setEffectController(new PlayerEffectController(player));
		player.setFlyController(new FlyController(player));
		PlayerStatFunctions.addPredefinedStatFunctions(player);
		player.setQuestStateList(PlayerQuestListDAO.load(player));
		player.setRecipeList(PlayerRecipesDAO.load(player.getObjectId()));
		Storage accWarehouse = account.getAccountWarehouse();
		player.setStorage(accWarehouse, StorageType.ACCOUNT_WAREHOUSE);
		Storage inventory = InventoryDAO.loadStorage(playerObjId, StorageType.CUBE);
		ItemService.loadItemStones(inventory.getItems());
		player.setStorage(inventory, StorageType.CUBE);
		for (int petBagId = StorageType.PET_BAG_MIN; petBagId <= StorageType.PET_BAG_MAX; petBagId++) {
			Storage petBag = InventoryDAO.loadStorage(playerObjId, StorageType.getStorageTypeById(petBagId));
			ItemService.loadItemStones(petBag.getItems());
			player.setStorage(petBag, StorageType.getStorageTypeById(petBagId));
		}
		Storage warehouse = InventoryDAO.loadStorage(playerObjId, StorageType.REGULAR_WAREHOUSE);
		ItemService.loadItemStones(warehouse.getItems());
		player.setStorage(warehouse, StorageType.REGULAR_WAREHOUSE);
		player.getEquipment().onLoadApplyEquipmentStats();
		PlayerPunishmentsDAO.loadPlayerPunishments(player, PunishmentType.PRISON);
		PlayerPunishmentsDAO.loadPlayerPunishments(player, PunishmentType.GATHER);
		player.getController().updatePassiveStats();
		PlayerEffectsDAO.loadPlayerEffects(player);
		PlayerCooldownsDAO.loadPlayerCooldowns(player);
		ItemCooldownsDAO.loadItemCooldowns(player);
		PortalCooldownsDAO.loadPortalCooldowns(player);
		PlayerBindPointDAO.loadBindPoint(player);
		CraftCooldownsDAO.loadCraftCooldowns(player);
		if (player.getCommonData().getTitleId() > 0) {
			TitleChangeListener.onTitleChange(player.getGameStats(), player.getCommonData().getTitleId(), true);
		}
		PlayerLifeStatsDAO.loadPlayerLifeStat(player);
		PlayerEmotionListDAO.loadEmotions(player);
		PlayerEquipmentSettingDAO.loadEquipmentSetting(player);
		if (CacheConfig.CACHE_PLAYERS) {
			playerCache.put(playerObjId, player);
		}
		return player;
	}

	public static Player newPlayer(PlayerCommonData playerCommonData, PlayerAppearance playerAppearance, Account account) {
		PlayerInitialData playerInitialData = DataManager.PLAYER_INITIAL_DATA;
		LocationData ld = playerCommonData.getPlayerClass() == PlayerClass.MONK ? playerInitialData.getTelosSpawnLocation(playerCommonData.getRace()) : playerInitialData.getSpawnLocation(playerCommonData.getRace());
		WorldPosition position = World.getInstance().createPosition(ld.getMapId(), ld.getX(), ld.getY(), ld.getZ(), ld.getHeading(), 0);
		playerCommonData.setPosition(position);
		Player newPlayer = new Player(new PlayerController(), playerCommonData, playerAppearance, account);
		newPlayer.setSkillList(new PlayerSkillList());
		SkillLearnService.addNewSkills(newPlayer);
		PlayerCreationData playerCreationData = playerInitialData.getPlayerCreationData(playerCommonData.getPlayerClass());
		Storage playerInventory = new PlayerStorage(StorageType.CUBE);
		Storage regularWarehouse = new PlayerStorage(StorageType.REGULAR_WAREHOUSE);
		Storage accountWarehouse = new PlayerStorage(StorageType.ACCOUNT_WAREHOUSE);
		Equipment equipment = new Equipment(newPlayer);
		if (playerCreationData != null) {
			List<ItemType> items = playerCreationData.getItems();
			for (ItemType itemType : items) {
				if (itemType.getRace() != Race.PC_ALL && itemType.getRace() != newPlayer.getRace()) {
					continue;
				}
				int itemId = itemType.getTemplate().getTemplateId();
				Item item = ItemFactory.newItem(itemId, itemType.getCount());
				if (item == null) {
					continue;
				}
				ItemTemplate itemTemplate = item.getItemTemplate();
				if ((itemTemplate.isArmor() || itemTemplate.isWeapon()) && !(equipment.isSlotEquipped(itemTemplate.getItemSlot()))) {
					item.setEquipped(true);
					ItemSlot itemSlot = ItemSlot.getSlotFor(itemTemplate.getItemSlot());
					item.setEquipmentSlot(itemSlot.getSlotIdMask());
					equipment.onLoadHandler(item);
				} else {
					playerInventory.onLoadHandler(item);
				}
			}
		}
		newPlayer.setStorage(playerInventory, StorageType.CUBE);
		newPlayer.setStorage(regularWarehouse, StorageType.REGULAR_WAREHOUSE);
		newPlayer.setStorage(accountWarehouse, StorageType.ACCOUNT_WAREHOUSE);
		newPlayer.setEquipment(equipment);
		newPlayer.setMailbox(new Mailbox(newPlayer));
		for (int petBagId = StorageType.PET_BAG_MIN; petBagId <= StorageType.PET_BAG_MAX; petBagId++) {
			Storage petBag = new PlayerStorage(StorageType.getStorageTypeById(petBagId));
			newPlayer.setStorage(petBag, StorageType.getStorageTypeById(petBagId));
		}
		playerInventory.setPersistentState(PersistentState.UPDATE_REQUIRED);
		equipment.setPersistentState(PersistentState.UPDATE_REQUIRED);
		return newPlayer;
	}

	public static boolean cancelPlayerDeletion(PlayerAccountData accData) {
		if (accData.getDeletionDate() == null) {
			return true;
		} if (accData.getDeletionDate().getTime() > System.currentTimeMillis()) {
			accData.setDeletionDate(null);
			storeDeletionTime(accData);
			return true;
		}
		return false;
	}

	public static void deletePlayer(PlayerAccountData accData) {
		if (accData.getDeletionDate() != null) {
			return;
		}
		accData.setDeletionDate(new Timestamp(System.currentTimeMillis() + 5 * 60 * 1000));
		storeDeletionTime(accData);
	}

	public static void deletePlayerFromDB(int playerId) {
		InventoryDAO.deletePlayerItems(playerId);
		PlayerDAO.deletePlayer(playerId);
	}

	public static int deleteAccountsCharsFromDB(int accountId) {
		List<Integer> charIds = PlayerDAO.getPlayerOidsOnAccount(accountId);
		for (int playerId : charIds) {
			deletePlayerFromDB(playerId);
		}
		return charIds.size();
	}

	private static void storeDeletionTime(PlayerAccountData accData) {
		PlayerDAO.updateDeletionTime(accData.getPlayerCommonData().getPlayerObjId(), accData.getDeletionDate());
	}

	public static void storeCreationTime(int objectId, Timestamp creationDate) {
		PlayerDAO.storeCreationTime(objectId, creationDate);
	}

	public static void addMacro(Player player, int macroOrder, String macroXML) {
		if (player.getMacroList().addMacro(macroOrder, macroXML)) {
			PlayerMacrossesDAO.addMacro(player.getObjectId(), macroOrder, macroXML);
		} else {
			PlayerMacrossesDAO.updateMacro(player.getObjectId(), macroOrder, macroXML);
		}
	}

	public static void removeMacro(Player player, int macroOrder) {
		if (player.getMacroList().removeMacro(macroOrder)) {
			PlayerMacrossesDAO.deleteMacro(player.getObjectId(), macroOrder);
		}
	}

	public static Player getCachedPlayer(int playerObjectId) {
		return playerCache.get(playerObjectId);
	}

	public static String getPlayerName(Integer objectId) {
		return getPlayerNames(Collections.singleton(objectId)).get(objectId);
	}

	public static Map<Integer, String> getPlayerNames(Collection<Integer> playerObjIds) {
		if (GenericValidator.isBlankOrNull(playerObjIds)) {
			return Collections.emptyMap();
		}
		final Map<Integer, String> result = Maps.newHashMap();
		final Set<Integer> playerObjIdsCopy = Sets.newHashSet(playerObjIds);
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player object) {
				if (playerObjIdsCopy.contains(object.getObjectId())) {
					result.put(object.getObjectId(), object.getName());
					playerObjIdsCopy.remove(object.getObjectId());
				}
			}
		});
		result.putAll(PlayerDAO.getPlayerNames(playerObjIdsCopy));
		return result;
	}
}
