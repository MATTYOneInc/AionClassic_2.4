package com.aionemu.loginserver.taskmanager.trigger.implementations;

import com.aionemu.loginserver.taskmanager.trigger.TaskFromDBTrigger;
import com.aionemu.loginserver.utils.ThreadPoolManager;
import java.util.Calendar;

public class FixedInTimeTrigger extends TaskFromDBTrigger {

	private final int DAY_IN_MSEC = 86400000;

	public boolean isValidTrigger() {
		return this.params.length == 1;
	}

	public void initTrigger() {
		String[] time = this.params[0].split(":");
		int hour = Integer.parseInt(time[0]);
		int minute = Integer.parseInt(time[1]);
		int second = Integer.parseInt(time[2]);

		Calendar calendar = Calendar.getInstance();
		calendar.set(11, hour);
		calendar.set(12, minute);
		calendar.set(13, second);

		long delay = calendar.getTimeInMillis() - System.currentTimeMillis();

		if (delay < 0) {
			delay += DAY_IN_MSEC;
		}
		ThreadPoolManager.getInstance().scheduleAtFixedRate(this, delay, DAY_IN_MSEC);
	}
}
