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

import com.aionemu.gameserver.cache.HTMLCache;
import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.configs.main.*;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.PlayerPasskeyDAO;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.account.CharacterPasskey.ConnectType;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerCommonData;
import com.aionemu.gameserver.model.gameobjects.player.emotion.Emotion;
import com.aionemu.gameserver.model.gameobjects.player.motion.Motion;
import com.aionemu.gameserver.model.gameobjects.player.title.Title;
import com.aionemu.gameserver.model.items.storage.IStorage;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.network.aion.serverpackets.need.S_LOAD_EQUIPMENT_CHANGE;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.abyss.AbyssSkillService;
import com.aionemu.gameserver.services.craft.RelinquishCraftStatus;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.legion.LegionService;
import com.aionemu.gameserver.services.mail.MailService;
import com.aionemu.gameserver.services.toypet.PetService;
import com.aionemu.gameserver.taskmanager.tasks.ExpireTimerTask;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.utils.audit.GMService;
import com.aionemu.gameserver.utils.rates.Rates;
import com.aionemu.gameserver.world.World;
import javolution.util.FastList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

public final class PlayerEnterWorldService
{
	private static final Logger log = LoggerFactory.getLogger("GAMECONNECTION_LOG");
	private static final String serverName = "Welcome to Aion Classic";
	private static final String serverIntro = "Aion Classic is now live!";
	private static final Set<Integer> pendingEnterWorld = new HashSet<Integer>();
	static ScheduledFuture<?> adv = null;



	public static final void startEnterWorld(final int objectId, final AionConnection client) {
		PlayerAccountData playerAccData = client.getAccount().getPlayerAccountData(objectId);
		Timestamp lastOnline = playerAccData.getPlayerCommonData().getLastOnline();
		if (lastOnline != null && client.getAccount().getAccessLevel() < AdminConfig.GM_LEVEL) {
			if (System.currentTimeMillis() - lastOnline.getTime() < (GSConfig.CHARACTER_REENTRY_TIME * 1000)) {
				client.sendPacket(new S_ENTER_WORLD_CHECK((byte) 6)); // 20 sec time
				return;
			}
		}
		validateAndEnterWorld(objectId, client);
	}

	private static final void showPasskey(final int objectId, final AionConnection client) {
		client.getAccount().getCharacterPasskey().setConnectType(ConnectType.ENTER);
		client.getAccount().getCharacterPasskey().setObjectId(objectId);
		boolean isExistPasskey = PlayerPasskeyDAO.existCheckPlayerPasskey(client.getAccount().getId());
		if (!isExistPasskey) {
			client.sendPacket(new S_2ND_PASSWORD(0));
		} else {
			client.sendPacket(new S_2ND_PASSWORD(1));
		}
	}

	private static final void validateAndEnterWorld(final int objectId, final AionConnection client) {
		synchronized (pendingEnterWorld) {
			if (pendingEnterWorld.contains(objectId)) {
				log.warn("Skipping enter world " + objectId);
				return;
			}
			pendingEnterWorld.add(objectId);
		}
		int delay = 0;
		if (World.getInstance().findPlayer(objectId) != null) {
			delay = 15000;
			log.warn("Postponed enter world " + objectId);
		}
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				try {
					Player player = World.getInstance().findPlayer(objectId);
					if (player != null) {
						AuditLogger.info(player, "Duplicate player in world");
						client.close(new S_ASK_QUIT_RESULT(), false);
						return;
					}
					enterWorld(client, objectId);
				}
				catch (Throwable ex) {
					log.error("Error during enter world " + objectId, ex);
				}
				finally {
					synchronized (pendingEnterWorld) {
						pendingEnterWorld.remove(objectId);
					}
				}
			}
		}, delay);
	}

	public static final void enterWorld(AionConnection client, int objectId) {
		Account account = client.getAccount();
		PlayerAccountData playerAccData = client.getAccount().getPlayerAccountData(objectId);
		if (playerAccData == null) {
			return;
		}
		final Player player = PlayerService.getPlayer(objectId, account);
		if (player != null && client.setActivePlayer(player)) {
			player.setClientConnection(client);
			log.info("[MAC_AUDIT] Player " + player.getName() + " (account " + account.getName() + ") has entered world with " + client.getMacAddress() + " MAC.");
			World.getInstance().storeObject(player);
			if (playerAccData.getPlayerCommonData().getLastOnline() != null) {
				long lastOnline = playerAccData.getPlayerCommonData().getLastOnline().getTime();
				PlayerCommonData pcd = player.getCommonData();
				long secondsOffline = (System.currentTimeMillis() / 1000) - lastOnline / 1000;
				if (pcd.isReadyForSalvationPoints()) {
					//The level of "Energy of Salvation" will now be maintained up to 4 hour after disconnect.
					if (secondsOffline > 240 * 60) {
						player.getCommonData().resetSalvationPoints();
					}
				} if (pcd.isReadyForReposteEnergy()) {
					pcd.updateMaxReposte();
					if (secondsOffline > 60 * 60) {
						double hours = Math.round(secondsOffline / 3600.0);
						long maxRespose = player.getCommonData().getMaxReposteEnergy();
						long reposePerHour = (maxRespose * 4) / 100;
						if (hours > 24.0) {
							hours = 24.0;
						}
						long addResposeEnergy = Math.round(hours * reposePerHour);
						addResposeEnergy = (long) ((float)addResposeEnergy * 1.1);
						pcd.addReposteEnergy(addResposeEnergy > maxRespose ? maxRespose : addResposeEnergy);
					}
				} if (System.currentTimeMillis() / 1000 - lastOnline > 300) {
					player.getCommonData().setDp(0);
				}
			}
			StigmaService.onPlayerLogin(player);
			InstanceService.onPlayerLogin(player);
			AbyssSkillService.onEnterWorld(player);
			SerialKillerService.getInstance().onLogin(player);
			client.sendPacket(new S_ADD_SKILL(player));
			if (player.getSkillCoolDowns() != null) {
                client.sendPacket(new S_LOAD_SKILL_COOLTIME((Map<Integer, Long>)player.getSkillCoolDowns()));
            } if (player.getItemCoolDowns() != null) {
				client.sendPacket(new S_LOAD_ITEM_COOLTIME(player.getItemCoolDowns()));
			}
			FastList<QuestState> questList = FastList.newInstance();
			FastList<QuestState> completeQuestList = FastList.newInstance();
			for (QuestState qs : player.getQuestStateList().getAllQuestState()) {
				if (qs.getStatus() == QuestStatus.NONE && qs.getCompleteCount() == 0) {
					continue;
				} if (qs.getStatus() != QuestStatus.COMPLETE && qs.getStatus() != QuestStatus.NONE) {
					questList.add(qs);
				} if (qs.getCompleteCount() > 0) {
					completeQuestList.add(qs);
				}
			}
			client.sendPacket(new S_LOAD_FINISHEDQUEST(completeQuestList));
			client.sendPacket(new S_LOAD_WORKINGQUEST(questList));
			client.sendPacket(new S_TITLE(player.getCommonData().getTitleId()));
			client.sendPacket(new S_CUSTOM_ANIM(player.getMotions().getMotions().values()));
			client.sendPacket(new S_CUSTOM_ANIM(player.getObjectId(), player.getMotions().getActiveMotions()));
			BattlePassService.getInstance().onEnterWorld(player);
			client.sendPacket(new S_ENTER_WORLD_CHECK());
			byte[] uiSettings = player.getPlayerSettings().getUiSettings();
			byte[] shortcuts = player.getPlayerSettings().getShortcuts();
			if (uiSettings != null) {
				client.sendPacket(new S_LOAD_CLIENT_SETTINGS(uiSettings, 0));
			} if (shortcuts != null) {
				client.sendPacket(new S_LOAD_CLIENT_SETTINGS(shortcuts, 1));
			}
			sendItemInfos(client, player);
			playerLoggedIn(player);
			client.sendPacket(new S_INSTANCE_DUNGEON_COOLTIMES(player, false, player.getCurrentTeam()));
			client.sendPacket(new S_CHANGE_CHANNEL(player.getPosition()));
			KiskService.getInstance().onLogin(player);
			player.getPosition().getWorld().getWorldMap(player.getWorldId()).getWorldHandler().onPlayerLogOut(player);
			World.getInstance().preSpawn(player);
			client.sendPacket(new S_WORLD(player));
			client.sendPacket(new S_TIME());
			client.sendPacket(new S_TITLE(player));
			client.sendPacket(new S_ADDREMOVE_SOCIAL((byte) 0, player.getEmotions().getEmotions()));
			SiegeService.getInstance().onPlayerLogin(player);
			client.sendPacket(new S_TAX_INFO());
			client.sendPacket(new S_ABYSS_POINT(player.getAbyssRank()));
			//Intro Msg.
			PacketSendUtility.sendWhiteMessage(player, serverName);
			PacketSendUtility.sendWhiteMessage(player, serverIntro);
			player.setRates(Rates.getRatesFor(client.getAccount().getMembership()));
			//Buff.
			/*if (player.getMembership() >= 0) {
				SkillEngine.getInstance().applyEffectDirectly(2358, player, player, 0); //[Event] A Noble's Blessing.
			}*/
			PlayerAllianceService.onPlayerLogin(player);
			if (player.isInPrison()) {
				PunishmentService.updatePrisonStatus(player);
			} if (player.isNotGatherable()) {
				PunishmentService.updateGatherableStatus(player);
			}
			PlayerGroupService.onPlayerLogin(player);
			PetService.getInstance().onPlayerLogin(player);
			MailService.getInstance().onPlayerLogin(player);
			BrokerService.getInstance().onPlayerLogin(player);
			sendMacroList(client, player);
			client.sendPacket(new S_CUR_STATUS((byte)1));
			if (player.isLegionMember()) {
				LegionService.getInstance().onLogin(player);
			}
			EventWindowService.getInstance().onEnterWorld(player);
			SielEnergyService.getInstance().onLogin(player);
			client.sendPacket(new S_RECIPE_LIST(player.getRecipeList().getRecipeList()));
			//PetitionService.getInstance().onPlayerLogin(player);
			if (AutoGroupConfig.AUTO_GROUP_ENABLED) {
				AutoGroupService.getInstance().onPlayerLogin(player);
			}
			ClassChangeService.showClassChangeDialog(player);
			GMService.getInstance().onPlayerLogin(player);
			player.getLifeStats().updateCurrentStats();
			player.getEquipment().checkRankLimitItems();
			if (HTMLConfig.ENABLE_HTML_WELCOME) {
				HTMLService.showHTML(player, HTMLCache.getInstance().getHTML("welcome.xhtml"));
			}
			player.getNpcFactions().sendDailyQuest();
			if (HTMLConfig.ENABLE_GUIDES) {
				HTMLService.onPlayerLogin(player);
			} for (StorageType st : StorageType.values()) {
				if (st == StorageType.LEGION_WAREHOUSE) {
					continue;
				}
				IStorage storage = player.getStorage(st.getId());
				if (storage != null) {
					for (Item item : storage.getItemsWithKinah()) {
						if (item.getExpireTime() > 0) {
							ExpireTimerTask.getInstance().addTask(item, player);
						}
					}
				}
			} for (Item item : player.getEquipment().getEquippedItems()) {
				if (item.getExpireTime() > 0) {
					ExpireTimerTask.getInstance().addTask(item, player);
				}
			} for (Motion motion : player.getMotions().getMotions().values()) {
				if (motion.getExpireTime() != 0) {
					ExpireTimerTask.getInstance().addTask(motion, player);
				}
			} for (Emotion emotion : player.getEmotions().getEmotions()) {
				if (emotion.getExpireTime() != 0) {
					ExpireTimerTask.getInstance().addTask(emotion, player);
				}
			} for (Title title : player.getTitleList().getTitles()) {
				if (title.getExpireTime() != 0) {
					ExpireTimerTask.getInstance().addTask(title, player);
				}
			}
			player.getController().addTask(TaskId.PLAYER_UPDATE, ThreadPoolManager.getInstance().scheduleAtFixedRate(new GeneralUpdateTask(player.getObjectId()), PeriodicSaveConfig.PLAYER_GENERAL * 1000, PeriodicSaveConfig.PLAYER_GENERAL * 1000));
			player.getController().addTask(TaskId.INVENTORY_UPDATE, ThreadPoolManager.getInstance().scheduleAtFixedRate(new ItemUpdateTask(player.getObjectId()), PeriodicSaveConfig.PLAYER_ITEMS * 1000, PeriodicSaveConfig.PLAYER_ITEMS * 1000));
			///On the official servers when a player is afk, he can gain energy of repose value of 5% every 20Min!!!
			player.getController().addTask(TaskId.REPOSE_ENERGY_TASK, ThreadPoolManager.getInstance().scheduleAtFixedRate(new ReposeEnergyTask(player.getObjectId()), 1200 * 1000, 1200 * 1000));
			SurveyService.getInstance().showAvailable(player);
			if (EventsConfig.ENABLE_EVENT_SERVICE) {
				EventService.getInstance().onPlayerLogin(player);
			}
			BoostEventService.getInstance().boostEventLogin(player);
			RelinquishCraftStatus.removeExcessCraftStatus(player, false);
			BlackCloudTradeService.getInstance().onLogin(player);
			//"Energy Of Repose"
			if (player.getCommonData().getCurrentReposteEnergy() != 0) {
				client.sendPacket(new S_STATUS(player));
			}
			//Fucking 2.4 holy shit chat
			client.sendPacket(new S_CHAT_ACCUSE(player));
			client.sendPacket(new S_SPAM_FILTER_FLAG(player));
			client.sendPacket(new S_CHANNEL_CHATTING_BLACKLIST_SETTING());
			client.sendPacket(new S_INSTANCE_DUNGEON_COOLTIMES(player, 0));


			if (!player.getEquipmentSettingList().getEquipmentSetting().isEmpty()) {
				client.sendPacket(new S_LOAD_EQUIPMENT_CHANGE(player.getEquipmentSettingList().getEquipmentSetting()));
			}

			PlayerWardrobeService.getInstance().onEnterWorld(player);
			ArcadeUpgradeService.getInstance().onEnterWorld(player);
		} else {
			log.info("[DEBUG] Enter World" + objectId + ", Player: " + player);
		}
	}

	private static void sendItemInfos(AionConnection client, Player player) {
		int questExpands = player.getQuestExpands();
		int npcExpands = player.getNpcExpands();
		player.getInventory().setLimit(StorageType.CUBE.getLimit() + (questExpands + npcExpands) * 9);
		player.getWarehouse().setLimit(StorageType.REGULAR_WAREHOUSE.getLimit() + player.getWarehouseSize() * 8);
		Storage inventory = player.getInventory();
		List<Item> equipedItems = player.getEquipment().getEquippedItems();
		if (equipedItems.size() != 0) {
			client.sendPacket(new S_LOAD_INVENTORY(true, player.getEquipment().getEquippedItems(), npcExpands, questExpands, player));
		}
		List<Item> unequipedItems = inventory.getItemsWithKinah();
		int itemsSize = unequipedItems.size();
		if (itemsSize != 0) {
			int index = 0;
			while (index + 10 < itemsSize) {
				client.sendPacket(new S_LOAD_INVENTORY(false,unequipedItems.subList(index, index + 10), npcExpands, questExpands, player));
				index += 10;
			}
			client.sendPacket(new S_LOAD_INVENTORY(false, unequipedItems.subList(index, itemsSize), npcExpands, questExpands, player));
		}
		client.sendPacket(new S_LOAD_INVENTORY());
		client.sendPacket(new S_STATUS(player));
		client.sendPacket(S_EVENT.stigmaSlots(player.getCommonData().getAdvencedStigmaSlotSize()));
	}

	private static void sendMacroList(AionConnection client, Player player) {
		client.sendPacket(new S_LOAD_MACRO(player, false));
		if (player.getMacroList().getSize() > 7) {
			client.sendPacket(new S_LOAD_MACRO(player, true));
		}
	}

	private static void playerLoggedIn(Player player) {
		log.info("Player Logged In: " + player.getName() + " Account: " + player.getClientConnection().getAccount().getName());
		player.getCommonData().setOnline(true);
		PlayerDAO.onlinePlayer(player, true);
		player.onLoggedIn();
		player.setOnlineTime();
	}
}
