package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;

public class CM_E3_UNK extends AionClientPacket
{
    public CM_E3_UNK(int opcode, AionConnection.State state, AionConnection.State... restStates) {
        super(opcode, state, restStates);
    }
	
    @Override
    protected void readImpl() {
    }
	
    @Override
    protected void runImpl() {
    }
}