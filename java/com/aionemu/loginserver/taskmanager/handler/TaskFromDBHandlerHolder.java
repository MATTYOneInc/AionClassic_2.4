package com.aionemu.loginserver.taskmanager.handler;

import com.aionemu.loginserver.taskmanager.handler.implementations.RestartHandler;
import com.aionemu.loginserver.taskmanager.handler.implementations.ShutdownHandler;

public enum TaskFromDBHandlerHolder {
	SHUTDOWN(ShutdownHandler.class),
	RESTART(RestartHandler.class);

	private Class<? extends TaskFromDBHandler> taskClass;

	private TaskFromDBHandlerHolder(Class<? extends TaskFromDBHandler> taskClass) {
		this.taskClass = taskClass;
	}

	public Class<? extends TaskFromDBHandler> getTaskClass() {
		return this.taskClass;
	}
}
