package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;

/**
 * Created by wanke on 14/02/2017.
 */

public class CM_E0_UNK extends AionClientPacket
{
    public CM_E0_UNK(int opcode, AionConnection.State state, AionConnection.State... restStates) {
        super(opcode, state, restStates);
    }

    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
    }
}