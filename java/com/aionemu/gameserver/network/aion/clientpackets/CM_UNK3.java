package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;

public class CM_UNK3 extends AionClientPacket {


	public CM_UNK3(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	protected void readImpl() {
	}

	protected void runImpl() {
	}
}
