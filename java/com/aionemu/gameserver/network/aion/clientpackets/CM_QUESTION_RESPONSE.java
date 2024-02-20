package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_QUESTION_RESPONSE extends AionClientPacket
{
	private static final Logger log = LoggerFactory.getLogger(CM_GUILD.class);
	
	private int questionid;
	private int response;
	@SuppressWarnings("unused")
	private int senderid;
	
	public CM_QUESTION_RESPONSE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		questionid = readD();
		response = readC();
		readC();
		readH();
		senderid = readD();
		readD();
		readH();
	}
	
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player.isTrading()) {
			return;
		}
		player.getResponseRequester().respond(questionid, response);
	}
}