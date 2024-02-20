package com.aionemu.gameserver.utils.chathandlers;

import com.aionemu.commons.scripting.classlistener.AggregatedClassListener;
import com.aionemu.commons.scripting.classlistener.OnClassLoadUnloadListener;
import com.aionemu.commons.scripting.classlistener.ScheduledTaskClassListener;
import com.aionemu.commons.scripting.scriptmanager.ScriptManager;
import com.aionemu.commons.utils.PropertiesUtils;
import com.aionemu.gameserver.GameServerError;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.model.GameEngine;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author KID
 * @Modified Rolandas
 */
public class ChatProcessor implements GameEngine {

	private static final Logger log = LoggerFactory.getLogger("ADMINAUDIT_LOG");
	private static ChatProcessor instance = new ChatProcessor();
	private Map<String, ChatCommand> commands = new FastMap<String, ChatCommand>();
	private Map<String, Byte> accessLevel = new FastMap<String, Byte>();
	private ScriptManager sm = new ScriptManager();
	private Exception loadException = null;

	public static ChatProcessor getInstance() {
		return instance;
	}

	@Override
	public void load(CountDownLatch progressLatch) {
		try {
			log.info("Chat processor load started");
			init(sm, this);
		}
		finally {
			if (progressLatch != null)
				progressLatch.countDown();
		}
	}

	@Override
	public void shutdown() {
	}

	private ChatProcessor() {
	}

	private ChatProcessor(ScriptManager scriptManager) {
		init(scriptManager, this);
	}

	private void init(final ScriptManager scriptManager, ChatProcessor processor) {
		loadLevels();

		AggregatedClassListener acl = new AggregatedClassListener();
		acl.addClassListener(new OnClassLoadUnloadListener());
		acl.addClassListener(new ScheduledTaskClassListener());
		acl.addClassListener(new ChatCommandsLoader(processor));
		scriptManager.setGlobalClassListener(acl);

		final File[] files = new File[] { new File("./data/scripts/system/adminhandlers.xml"), new File("./data/scripts/system/playerhandlers.xml") };
		final CountDownLatch loadLatch = new CountDownLatch(files.length);

		for (int i = 0; i < files.length; i++) {
			final int index = i;
			ThreadPoolManager.getInstance().execute(new Runnable() {

				@Override
				public void run() {
					try {
						scriptManager.load(files[index]);
					}
					catch (Exception e) {
						loadException = e;
					}
					finally {
						loadLatch.countDown();
					}
				}
			});
		}

		try {
			loadLatch.await();
		}
		catch (InterruptedException e1) {
		}
		if (loadException != null) {
			throw new GameServerError("Can't initialize chat handlers.", loadException);
		}
	}

	public void registerCommand(ChatCommand cmd) {
		if (commands.containsKey(cmd.getAlias())) {
			log.warn("Command " + cmd.getAlias() + " is already registered. Fail");
			return;
		}

		if (!accessLevel.containsKey(cmd.getAlias())) {
			log.warn("Command " + cmd.getAlias() + " do not have access level. Fail");
			return;
		}

		cmd.setAccessLevel(accessLevel.get(cmd.getAlias()));
		commands.put(cmd.getAlias(), cmd);
	}

	public void reload() {
		ScriptManager tmpSM;
		final ChatProcessor adminCP;
		Map<String, ChatCommand> backupCommands = new FastMap<String, ChatCommand>(commands);
		commands.clear();
		loadException = null;

		try {
			tmpSM = new ScriptManager();
			adminCP = new ChatProcessor(tmpSM);
		}
		catch (Throwable e) {
			commands = backupCommands;
			throw new GameServerError("Can't reload chat handlers.", e);
		}

		if (tmpSM != null && adminCP != null) {
			backupCommands.clear();
			sm.shutdown();
			sm = null;
			sm = tmpSM;
			instance = adminCP;
		}
	}

	private void loadLevels() {
		accessLevel.clear();
		try {
			java.util.Properties props = PropertiesUtils.load("config/administration/commands.properties");

			for (Object key : props.keySet()) {
				String str = (String) key;
				accessLevel.put(str, Byte.valueOf(props.getProperty(str).trim()));
			}
		}
		catch (IOException e) {
			log.error("Can't read commands.properties", e);
		}
	}

	public boolean handleChatCommand(Player player, String text) {
		if (text.split(" ").length == 0)
			return false;
		if ((text.startsWith("//") && getCommand(text.substring(2)) instanceof AdminCommand)) {
			return (getCommand(text.substring(2))).process(player, text.substring(2));
		}
		else if (text.startsWith(".")
			&& (getCommand(text.substring(1)) instanceof PlayerCommand 
				|| (CustomConfig.ENABLE_ADMIN_DOT_COMMANDS && getCommand(text.substring(1)) instanceof AdminCommand))) {
			return (getCommand(text.substring(1))).process(player, text.substring(1));
		}
		else
			return false;
	}

	private ChatCommand getCommand(String text) {
		String alias = text.split(" ")[0];
		ChatCommand cmd = this.commands.get(alias);
		return cmd;
	}

	public void onCompileDone() {
		log.info("Loaded " + commands.size() + " commands.");
	}
}
