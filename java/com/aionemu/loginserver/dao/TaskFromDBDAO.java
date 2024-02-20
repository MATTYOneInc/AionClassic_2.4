package com.aionemu.loginserver.dao;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.loginserver.taskmanager.handler.TaskFromDBHandler;
import com.aionemu.loginserver.taskmanager.handler.TaskFromDBHandlerHolder;
import com.aionemu.loginserver.taskmanager.trigger.TaskFromDBTrigger;
import com.aionemu.loginserver.taskmanager.trigger.TaskFromDBTriggerHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author Divinity
 * @author nrg
 * @author Skunk
 */

public class TaskFromDBDAO
{
	private static final Logger log = LoggerFactory.getLogger(TaskFromDBDAO.class);

	public static ArrayList<TaskFromDBTrigger> getAllTasks()
	{
		final ArrayList<TaskFromDBTrigger> result = new ArrayList<TaskFromDBTrigger>();

		Connection con = null;

		PreparedStatement stmt = null;
		try {
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement("SELECT * FROM tasks ORDER BY id");

			ResultSet rset = stmt.executeQuery();

			while (rset.next()) {
				try {
					TaskFromDBTrigger trigger = TaskFromDBTriggerHolder.valueOf(rset.getString("trigger_type")).getTriggerClass().newInstance();
					TaskFromDBHandler handler = TaskFromDBHandlerHolder.valueOf(rset.getString("task_type")).getTaskClass().newInstance();

					handler.setTaskId(rset.getInt("id"));

					String execParamsResult = rset.getString("exec_param");
					if (execParamsResult != null)
						handler.setParams(rset.getString("exec_param").split(" "));

					trigger.setHandlerToTrigger(handler);

					String triggerParamsResult = rset.getString("trigger_param");
					if (triggerParamsResult != null)
						trigger.setParams(rset.getString("trigger_param").split(" "));

					result.add(trigger);

				} catch (InstantiationException | IllegalAccessException ex) {
					log.error(ex.getMessage(), ex);
				}
			}

			rset.close();
			stmt.close();
		} catch (SQLException e) {
			log.error("Loading tasks failed: ", e);
		} finally {
			DatabaseFactory.close(stmt, con);
		}

		return result;
	}
}
