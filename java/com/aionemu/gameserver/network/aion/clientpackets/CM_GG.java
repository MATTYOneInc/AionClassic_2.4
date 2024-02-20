package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;

/**
 * 
 * @author Ranastic
 *
 */
public class CM_GG extends AionClientPacket {

	private int size;
	byte[] data;
	
	public CM_GG(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		size = readD();
		data = readB(getRemainingBytes());
	}

	@Override
	protected void runImpl() {
		// TODO Auto-generated method stub
		
	}

}
