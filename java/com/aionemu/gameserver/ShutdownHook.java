package com.aionemu.gameserver;

import com.aionemu.commons.services.CronService;
import com.aionemu.commons.utils.ExitCode;
import com.aionemu.commons.utils.concurrent.RunnableStatsManager;
import com.aionemu.commons.utils.concurrent.RunnableStatsManager.SortBy;
import com.aionemu.gameserver.configs.main.ShutdownConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.services.PeriodicSaveService;
import com.aionemu.gameserver.services.player.PlayerLeaveWorldService;
import com.aionemu.gameserver.utils.*;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;
import com.aionemu.gameserver.world.World;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

import static com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE.STR_SERVER_SHUTDOWN;

public class ShutdownHook extends Thread
{
	private static final Logger log = LoggerFactory.getLogger(ShutdownHook.class);
	
	public static ShutdownHook getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	@Override
	public void run() {
		if (ShutdownConfig.HOOK_MODE == 1) {
			shutdownHook(ShutdownConfig.HOOK_DELAY, ShutdownConfig.ANNOUNCE_INTERVAL, ShutdownMode.SHUTDOWN);
		} else if (ShutdownConfig.HOOK_MODE == 2) {
			shutdownHook(ShutdownConfig.HOOK_DELAY, ShutdownConfig.ANNOUNCE_INTERVAL, ShutdownMode.RESTART);
		}
	}
	
	public static enum ShutdownMode {
		NONE("terminating"),
		SHUTDOWN("shutting down"),
		RESTART("restarting");
		
		private final String text;
		
		private ShutdownMode(String text) {
			this.text = text;
		}
		
		public String getText() {
			return text;
		}
	}
	
	private void sendShutdownMessage(int seconds) {
		try {
			Iterator<Player> onlinePlayers = World.getInstance().getPlayersIterator();
			if (!onlinePlayers.hasNext()) {
				return;
			} while (onlinePlayers.hasNext()) {
				Player player = onlinePlayers.next();
				if (player != null && player.getClientConnection() != null) {
					player.getClientConnection().sendPacket(STR_SERVER_SHUTDOWN(String.valueOf(seconds)));
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	private void sendShutdownStatus(boolean status) {
		try {
			Iterator<Player> onlinePlayers = World.getInstance().getPlayersIterator();
			if (!onlinePlayers.hasNext()) {
				return;
			} while (onlinePlayers.hasNext()) {
				Player player = onlinePlayers.next();
				if (player != null && player.getClientConnection() != null) {
					player.getController().setInShutdownProgress(status);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	private void shutdownHook(int duration, int interval, ShutdownMode mode) {
		for (int i = duration; i >= interval; i -= interval) {
			try {
				if (World.getInstance().getPlayersIterator().hasNext()) {
					log.info("Runtime Is " + mode.getText() + " In " + i + " Seconds.");
					sendShutdownMessage(i);
					sendShutdownStatus(ShutdownConfig.SAFE_REBOOT);
				} else {
					log.info("Runtime Is " + mode.getText() + " Now ...");
					break;
				} if (i > interval) {
					sleep(interval * 1000);
				} else {
					sleep(i * 1000);
				}
			} catch (InterruptedException e) {
				return;
			}
		}
		LoginServer.getInstance().gameServerDisconnected();
		Iterator<Player> onlinePlayers;
		onlinePlayers = World.getInstance().getPlayersIterator();
		while (onlinePlayers.hasNext()) {
			Player activePlayer = onlinePlayers.next();
			try {
				PlayerLeaveWorldService.startLeaveWorld(activePlayer);
			} catch (Exception e) {
				log.error("Error while saving player " + e.getMessage());
			}
		}
		log.info("All Players Are Disconnected...");
		RunnableStatsManager.dumpClassStats(SortBy.AVG);
		PeriodicSaveService.getInstance().onShutdown();
		GameTimeManager.saveTime();
		CronService.getInstance().shutdown();
		ThreadPoolManager.getInstance().shutdown();
		if (mode == ShutdownMode.RESTART) {
			Runtime.getRuntime().halt(ExitCode.CODE_RESTART);
		} else {
			Runtime.getRuntime().halt(ExitCode.CODE_NORMAL);
		}
		log.info("Runtime is " + mode.getText() + " now...");
	}
	
	public void doShutdown(int delay, int announceInterval, ShutdownMode mode) {
		shutdownHook(delay, announceInterval, mode);
	}
	
	private static final class SingletonHolder {
		private static final ShutdownHook INSTANCE = new ShutdownHook();
	}
}