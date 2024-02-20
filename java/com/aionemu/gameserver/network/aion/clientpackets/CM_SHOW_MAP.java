package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.SerialKillerService;

public class CM_SHOW_MAP extends AionClientPacket
{
	public CM_SHOW_MAP(int opcode, State state, State... restStates) {
	    super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
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
        }
		SerialKillerService.getInstance().updateIcons(player);
	}
}