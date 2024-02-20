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
package com.aionemu.gameserver.model.items;

/**
 * @author ATracer
 */
public class ItemCooldown {

	/**
	 * time of next reuse
	 */
	private long time;
	/**
	 * Use delay in ms
	 */
	private int useDelay;

	/**
	 * @param time
	 * @param useDelay
	 */
	public ItemCooldown(long time, int useDelay) {
		this.time = time;
		this.useDelay = useDelay;
	}

	/**
	 * @return the time
	 */
	public long getReuseTime() {
		return time;
	}

	/**
	 * @return the useDelay
	 */
	public int getUseDelay() {
		return useDelay;
	}
}
