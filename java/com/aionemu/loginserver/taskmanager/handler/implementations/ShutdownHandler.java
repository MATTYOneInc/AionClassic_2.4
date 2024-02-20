package com.aionemu.loginserver.taskmanager.handler.implementations;

import com.aionemu.loginserver.Shutdown;
import com.aionemu.loginserver.taskmanager.handler.TaskFromDBHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownHandler extends TaskFromDBHandler {

	private static final Logger log = LoggerFactory.getLogger(ShutdownHandler.class);

	public boolean isValid() {
		return true;
	}

	public void trigger() {
		log.info("Task[" + taskId + "] launched : shutting down the server !");

		Shutdown shutdown = Shutdown.getInstance();
		shutdown.setRestartOnly(false);
		shutdown.start();
	}
}
