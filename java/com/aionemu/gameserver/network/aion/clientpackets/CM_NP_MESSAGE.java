package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_NP_MESSAGE extends AionClientPacket {

    private int code;
    private String message;
    private static Logger log = LoggerFactory.getLogger(CM_NP_MESSAGE.class);

    public CM_NP_MESSAGE(int opcode, AionConnection.State state, AionConnection.State... restStates) {
        super(opcode, state, restStates);
    }

    @Override
    protected void readImpl() {
        this.code = readC();
        this.message = readS(getRemainingBytes());
    }

    @Override
    protected void runImpl() {
        //log.info("[CM_NP_MESSAGE] : code : " + this.code + " Message -> " + this.message);
    }
}