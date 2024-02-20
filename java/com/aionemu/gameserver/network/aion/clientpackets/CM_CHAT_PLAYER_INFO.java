package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_ASK_INFO_RESULT;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

public class CM_CHAT_PLAYER_INFO extends AionClientPacket
{
	private String playerName;
	
	public CM_CHAT_PLAYER_INFO(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		playerName = readS();
	}
	
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		Player target = World.getInstance().findPlayer(playerName);
		if (target == null) {
			PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_ASK_PCINFO_LOGOFF);
			return;
		}
		if (!player.getKnownList().knowns(target))
			PacketSendUtility.sendPacket(player, new S_ASK_INFO_RESULT(target, false));
	}
}