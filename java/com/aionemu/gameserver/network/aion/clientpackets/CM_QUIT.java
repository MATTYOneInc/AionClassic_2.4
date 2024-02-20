package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.S_ASK_QUIT_RESULT;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.services.player.PlayerLeaveWorldService;

public class CM_QUIT extends AionClientPacket
{
	private boolean logout;
	
	public CM_QUIT(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		logout = readC() == 1;
	}
	
	@Override
	protected void runImpl() {
		AionConnection client = getConnection();
		Player player = null;
		if (client.getState() == State.IN_GAME) {
			player = client.getActivePlayer();
			if (!logout) {
				LoginServer.getInstance().aionClientDisconnected(client.getAccount().getId());
			}
			PlayerLeaveWorldService.startLeaveWorld(player);
			client.setActivePlayer(null);
		} if (logout) {
			if (player != null && player.isInEditMode()) {
				sendPacket(new S_ASK_QUIT_RESULT(true));
				player.setEditMode(false);
			} else {
				sendPacket(new S_ASK_QUIT_RESULT());
			}
		} else {
			client.close(new S_ASK_QUIT_RESULT(), false);
		}
	}
}