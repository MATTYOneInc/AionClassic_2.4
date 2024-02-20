/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.commons.callbacks;

/**
 * Interface that is used to mark callback priority when it's not default.<br>
 * Callback doesn't have to implement this interface if priority is default.<br>
 * Listeners with bigger priority are executed earlier.
 *
 * @author SoulKeeper
 */
public interface CallbackPriority
{
	/**
	 * Returns default priority of callback
	 */
	int DEFAULT_PRIORITY = 0;

	/**
	 * Returns callbacks priority
	 *
	 * @return priority of callback
	 */
	int getPriority();
}
