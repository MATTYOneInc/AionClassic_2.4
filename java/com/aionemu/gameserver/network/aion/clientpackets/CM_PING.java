package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_ALIVE;

public class CM_PING extends AionClientPacket
{
	public CM_PING(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		readH();
	}
	
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		long lastMS = getConnection().getLastPingTimeMS();
		if (lastMS > 0 && player != null) {
			long pingInterval = System.currentTimeMillis() - lastMS;
			if (pingInterval < 80 * 1000) {
				//getConnection().closeNow();
			}
		}
		getConnection().setLastPingTimeMS(System.currentTimeMillis());
		sendPacket(new S_ALIVE());
	}
}