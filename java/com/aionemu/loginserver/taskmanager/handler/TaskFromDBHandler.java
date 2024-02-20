package com.aionemu.loginserver.taskmanager.handler;

public abstract class TaskFromDBHandler {

	protected int taskId;
	protected String[] params = { "" };

	public int getTaskId() {
		return this.taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public String[] getParams() {
		return this.params;
	}

	public void setParams(String[] params) {
		this.params = params;
	}

	public abstract boolean isValid();

	public abstract void trigger();
}
