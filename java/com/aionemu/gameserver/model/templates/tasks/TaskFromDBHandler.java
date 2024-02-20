/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.templates.tasks;

import com.aionemu.gameserver.dao.TaskFromDBDAO;

/**
 * @author Divinity
 */
public abstract class TaskFromDBHandler implements Runnable {

	protected int id;
	protected String params[];

	/**
	 * Task's id
	 *
	 * @param int
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Task's param(s)
	 *
	 * @param params
	 *          String[]
	 */
	public void setParam(String params[]) {
		this.params = params;
	}

	/**
	 * The task's name This allow to check with the table column "task"
	 */
	public abstract String getTaskName();

	/**
	 * Check if the task's parameters are valid
	 *
	 * @return true if valid, false otherwise
	 */
	public abstract boolean isValid();

	protected void setLastActivation() {
		TaskFromDBDAO.setLastActivation(id);
	}
}
