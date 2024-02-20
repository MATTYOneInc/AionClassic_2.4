package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * 
 * @author Ranastic
 *
 */
public class S_REPLY_NP_LOGIN_GAMESVR extends AionServerPacket {

    @Override
    protected void writeImpl(AionConnection con) {
    	writeC(0);
    	writeC(1);
    }
}
