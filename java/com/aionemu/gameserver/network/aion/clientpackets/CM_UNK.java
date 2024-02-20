package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;

public class CM_UNK extends AionClientPacket {

	int size;

	public CM_UNK(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		readB(size = readD());
	}

	@Override
	protected void runImpl() {
	}
}
