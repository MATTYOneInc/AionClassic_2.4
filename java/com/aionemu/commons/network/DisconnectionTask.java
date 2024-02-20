/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 *  aion-emu is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-emu is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.commons.network;

/**
 * Disconnection Task that will be execute on <code>DisconnectionThreadPool</code>
 *
 * @author -Nemesiss-
 * @see com.aionemu.commons.network.DisconnectionThreadPool
 */
public class DisconnectionTask implements Runnable
{
	/**
	 * Connection that onDisconnect() method will be executed by <code>DisconnectionThreadPool</code>
	 *
	 * @see com.aionemu.commons.network.DisconnectionThreadPool
	 */
	private AConnection connection;

	/**
	 * Construct <code>DisconnectionTask</code>
	 *
	 * @param connection
	 */
	public DisconnectionTask(AConnection connection)
	{
		this.connection = connection;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		connection.onDisconnect();
	}
}
