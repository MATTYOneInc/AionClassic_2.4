package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;

/**
 * @author Ranastic (Encom)
 */

public class CM_TITLE_SET extends AionClientPacket
{
	private int titleId;
	
	public CM_TITLE_SET(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		titleId = readH();
	}
	
	@Override
	protected void runImpl() {
		final Player player = getConnection().getActivePlayer();
        if (player == null || !player.isSpawned()) {
            return;
        } if (player.isProtectionActive()) {
            player.getController().stopProtectionActiveTask();
        } if (player.isCasting()) {
            player.getController().cancelCurrentSkill();
        } if (player.getController().isInShutdownProgress()) {
            return;
        } if (titleId != 0xFFFF) {
			if (!player.getTitleList().contains(titleId)) {
				return;
			}
		}
		player.getTitleList().setDisplayTitle(titleId);
	}
}