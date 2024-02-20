package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionConnection.State;

public class CM_DISCONNECT extends AionClientPacket {

    boolean unk;

    /**
     * Constructs new instance of <tt>CM_DISCONNECT </tt> packet
     *
     * @param opcode
     */
    public CM_DISCONNECT(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readImpl() {
        unk = readC() == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runImpl() {

        if (unk) {
            AionConnection client = getConnection();
            /**
             * We should close connection but not forced
             */
            client.closeNow();
        }
    }
}