package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_S_REP_WEB_SESSIONKEY extends AionClientPacket {

	private static Logger log = LoggerFactory.getLogger(CM_S_REP_WEB_SESSIONKEY.class);
	
	private int unk;
	private String text;
	
	public CM_S_REP_WEB_SESSIONKEY(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		unk = readC();
		text = readS();
	}

	@Override
	protected void runImpl() {
		log.info(text);
	}
}
