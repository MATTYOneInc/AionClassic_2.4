package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_ASK_INFO_RESULT;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

/**
 * @author ginho1
 */
public class CM_CHAT_GROUP_INFO extends AionClientPacket {

	private String playerName;
	@SuppressWarnings("unused")
	private int unk;

	public CM_CHAT_GROUP_INFO(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		playerName = readS();
		unk = readD();
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		Player target = World.getInstance().findPlayer(playerName);
		if (target == null) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_ASK_PCINFO_LOGOFF);
			return;
		}
		PacketSendUtility.sendPacket(player, new S_ASK_INFO_RESULT(target, true));
	}
}