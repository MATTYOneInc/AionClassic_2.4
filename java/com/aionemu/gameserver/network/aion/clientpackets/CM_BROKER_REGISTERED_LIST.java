package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.BrokerService;

public class CM_BROKER_REGISTERED_LIST extends AionClientPacket
{
    @SuppressWarnings("unused")
    private int npcId;
	
    public CM_BROKER_REGISTERED_LIST(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }
	
    @Override
    protected void readImpl() {
		this.npcId = readD();
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
		BrokerService.getInstance().showRegisteredItems(player);
    }
}