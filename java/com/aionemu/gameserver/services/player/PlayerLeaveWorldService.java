package com.aionemu.gameserver.services.player;

import com.aionemu.gameserver.configs.main.AutoGroupConfig;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.dao.*;
import com.aionemu.gameserver.model.account.PlayerAccountData;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.summons.SummonMode;
import com.aionemu.gameserver.model.summons.UnsummonType;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.services.*;
import com.aionemu.gameserver.services.drop.DropService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.legion.LegionService;
import com.aionemu.gameserver.services.summons.SummonsService;
import com.aionemu.gameserver.services.toypet.PetSpawnService;
import com.aionemu.gameserver.taskmanager.tasks.ExpireTimerTask;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.audit.GMService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

public class PlayerLeaveWorldService
{
	private static final Logger log = LoggerFactory.getLogger(PlayerLeaveWorldService.class);

	public static final void startLeaveWorldDelay(final Player player, int delay) {
		player.getController().stopMoving();
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				startLeaveWorld(player);
			}
		}, delay);
	}

	public static final void startLeaveWorld(Player player) {
		log.info("Player Logged Out: " + player.getName() + " Account: " + (player.getClientConnection() != null ? player.getClientConnection().getAccount().getName() : "Disconnected"));
		FindGroupService.getInstance().removeFindGroup(player.getRace(), 0x00, player.getObjectId());
		FindGroupService.getInstance().removeFindGroup(player.getRace(), 0x04, player.getObjectId());
		player.onLoggedOut();
		BrokerService.getInstance().removePlayerCache(player);
		ExchangeService.getInstance().cancelExchange(player);
		RepurchaseService.getInstance().removeRepurchaseItems(player);
		if (AutoGroupConfig.AUTO_GROUP_ENABLED) {
			AutoGroupService.getInstance().onPlayerLogOut(player);
		}
		InstanceService.onLogOut(player);
		GMService.getInstance().onPlayerLogedOut(player);
		KiskService.getInstance().onLogout(player);
		player.getMoveController().abortMove();
		if (player.isLooting()) {
			DropService.getInstance().closeDropList(player, player.getLootingNpcOid());
		} if (player.isInPrison()) {
			long prisonTimer = System.currentTimeMillis() - player.getStartPrison();
			prisonTimer = player.getPrisonTimer() - prisonTimer;
			player.setPrisonTimer(prisonTimer);
			log.debug("Update prison timer to " + prisonTimer / 1000 + " seconds !");
		}
		PlayerEffectsDAO.storePlayerEffects(player);
		PlayerCooldownsDAO.storePlayerCooldowns(player);
		ItemCooldownsDAO.storeItemCooldowns(player);
		PlayerLifeStatsDAO.updatePlayerLifeStat(player);
		EventItemsDAO.storeItems(player);
		PlayerGroupService.onPlayerLogout(player);
		BoostEventService.getInstance().boostEventLogout(player);
		PlayerAllianceService.onPlayerLogout(player);
		SerialKillerService.getInstance().onLogout(player);
		LegionService.getInstance().LegionWhUpdate(player);
		player.getEffectController().removeAllEffects(true);
		player.getLifeStats().cancelAllTasks();
		if (player.getLifeStats().isAlreadyDead()) {
			if (player.isInInstance()) {
				PlayerReviveService.instanceRevive(player);
			} else {
				PlayerReviveService.bindRevive(player);
			}
		} else if (DuelService.getInstance().isDueling(player.getObjectId())) {
			DuelService.getInstance().loseDuel(player);
		}
		Summon summon = player.getSummon();
		if (summon != null) {
			SummonsService.doMode(SummonMode.RELEASE, summon, UnsummonType.LOGOUT);
		}
		PetSpawnService.dismissPet(player, true);
		if (player.getPostman() != null) {
			player.getPostman().getController().onDelete();
		}
		player.setPostman(null);
		PunishmentService.stopPrisonTask(player, true);
		PunishmentService.stopGatherableTask(player, true);
		if (player.isLegionMember()) {
			LegionService.getInstance().onLogout(player);
		}
		QuestEngine.getInstance().onLogOut(new QuestEnv(null, player, 0, 0));
		player.getController().delete();
		player.getCommonData().setOnline(false);
		player.getCommonData().setLastOnline(new Timestamp(System.currentTimeMillis()));
		player.setClientConnection(null);
		PlayerDAO.onlinePlayer(player, false);
		if (GSConfig.ENABLE_CHAT_SERVER) {
			ChatService.onPlayerLogout(player);
		}
		PlayerService.storePlayer(player);
		ExpireTimerTask.getInstance().removePlayer(player);
		if (player.getCraftingTask() != null) {
			player.getCraftingTask().stop(true);
		}
		player.getEquipment().setOwner(null);
		player.getInventory().setOwner(null);
		player.getWarehouse().setOwner(null);
		player.getStorage(StorageType.ACCOUNT_WAREHOUSE.getId()).setOwner(null);
		//****//
		PlayerAccountData pad = player.getPlayerAccount().getPlayerAccountData(player.getObjectId());
		pad.setEquipment(player.getEquipment().getEquippedItems());
		EventWindowService.getInstance().onLogout(player);
	}

	public static void tryLeaveWorld(Player player) {
		player.getMoveController().abortMove();
		if (player.getController().isInShutdownProgress()) {
			PlayerLeaveWorldService.startLeaveWorld(player);
		} else {
			int delay = 15;
			PlayerLeaveWorldService.startLeaveWorldDelay(player, (delay * 1000));
		}
	}
}
