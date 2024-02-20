package com.aionemu.loginserver.taskmanager.trigger;

import com.aionemu.loginserver.taskmanager.trigger.implementations.FixedInTimeTrigger;

public enum TaskFromDBTriggerHolder {
	FIXED_IN_TIME(FixedInTimeTrigger.class);

	private Class<? extends TaskFromDBTrigger> triggerClass;

	private TaskFromDBTriggerHolder(Class<? extends TaskFromDBTrigger> triggerClass) {
		this.triggerClass = triggerClass;
	}

	public Class<? extends TaskFromDBTrigger> getTriggerClass() {
		return this.triggerClass;
	}
}
