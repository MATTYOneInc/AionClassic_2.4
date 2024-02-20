package com.aionemu.loginserver.taskmanager.trigger;

import com.aionemu.loginserver.taskmanager.handler.TaskFromDBHandler;

public abstract class TaskFromDBTrigger implements Runnable {

	protected TaskFromDBHandler handlerToTrigger;
	protected String[] params = { "" };

	public int getTaskId() {
		return this.handlerToTrigger.getTaskId();
	}

	public TaskFromDBHandler getHandlerToTrigger() {
		return handlerToTrigger;
	}

	public void setHandlerToTrigger(TaskFromDBHandler handlerToTrigger) {
		this.handlerToTrigger = handlerToTrigger;
	}

	public String[] getParams() {
		return this.params;
	}

	public void setParams(String[] params) {
		this.params = params;
	}

	public boolean isValid() {
		return isValidTrigger() && handlerToTrigger.isValid();
	}

	public abstract boolean isValidTrigger();

	public abstract void initTrigger();

	public void run() {
		handlerToTrigger.trigger();
	}
}
