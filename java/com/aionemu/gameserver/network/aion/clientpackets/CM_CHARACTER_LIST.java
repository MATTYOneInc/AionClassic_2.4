package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_CHARACTER_LIST extends AionClientPacket
{
	private static Logger log = LoggerFactory.getLogger(CM_CHARACTER_LIST.class);
	
	private int playOk2;
	
	public CM_CHARACTER_LIST(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		playOk2 = readD();
	}
	
	@Override
	protected void runImpl() {
		boolean isGM = (getConnection()).getAccount().getAccessLevel() >= AdminConfig.GM_PANEL;
		sendPacket(new S_BUILDER_LEVEL(isGM));
		sendPacket(new S_CHARACTER_LIST(playOk2));
	}
}