package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.services.BrokerService;

/**
 * Created by wanke on 22/02/2017.
 */

public class CM_BROKER_ADD_ITEM extends AionClientPacket
{
    private int objectId;
	
    public CM_BROKER_ADD_ITEM(int opcode, AionConnection.State state, AionConnection.State... restStates) {
        super(opcode, state, restStates);
    }
	
    protected void readImpl() {
        this.objectId = readD();
    }
	
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
        BrokerService.getInstance().showAddItemWindow(player, objectId);
    }
}