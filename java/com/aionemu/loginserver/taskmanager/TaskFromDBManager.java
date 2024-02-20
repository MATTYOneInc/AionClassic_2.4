package com.aionemu.loginserver.taskmanager;

import com.aionemu.loginserver.dao.TaskFromDBDAO;
import com.aionemu.loginserver.taskmanager.trigger.TaskFromDBTrigger;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskFromDBManager {

	private static final Logger log = LoggerFactory.getLogger(TaskFromDBManager.class);
	private ArrayList<TaskFromDBTrigger> tasksList;

	private TaskFromDBManager() {
		this.tasksList = TaskFromDBDAO.getAllTasks();
		log.info("Loaded " + tasksList.size() + " task " + (tasksList.size() > 1 ? "s" : "") + " from the database");

		registerTaskInstances();
	}

	private void registerTaskInstances() {
		for (TaskFromDBTrigger trigger : this.tasksList)
			if (trigger.isValid()) {
				trigger.initTrigger();
			}
			else
				log.error("Invalid task from db with ID: " + trigger.getTaskId());
	}

	public static TaskFromDBManager getInstance() {
		return SingletonHolder.instance;
	}

	private static class SingletonHolder {

		protected static final TaskFromDBManager instance = new TaskFromDBManager();
	}
}
