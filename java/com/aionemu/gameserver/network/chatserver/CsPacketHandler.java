/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 *  aion-unique is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-unique is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.network.chatserver;

import com.aionemu.gameserver.network.chatserver.ChatServerConnection.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class CsPacketHandler
{
    private static final Logger log = LoggerFactory.getLogger(CsPacketHandler.class);
    private Map<State, Map<Integer, CsClientPacket>> packetPrototypes = new HashMap<State, Map<Integer, CsClientPacket>>();
	
    public CsClientPacket handle(ByteBuffer data, ChatServerConnection client) {
        State state = client.getState();
        int id = data.get() & 0xff;
        return getPacket(state, id, data, client);
    }
	
    public void addPacketPrototype(CsClientPacket packetPrototype, State... states) {
        for (State state : states) {
            Map<Integer, CsClientPacket> pm = packetPrototypes.get(state);
            if (pm == null) {
                pm = new HashMap<Integer, CsClientPacket>();
                packetPrototypes.put(state, pm);
            }
            pm.put(packetPrototype.getOpcode(), packetPrototype);
        }
    }
	
    private CsClientPacket getPacket(State state, int id, ByteBuffer buf, ChatServerConnection con) {
        CsClientPacket prototype = null;
        Map<Integer, CsClientPacket> pm = packetPrototypes.get(state);
        if (pm != null) {
            prototype = pm.get(id);
        } if (prototype == null) {
            unknownPacket(state, id);
            return null;
        }
        CsClientPacket res = prototype.clonePacket();
        res.setBuffer(buf);
        res.setConnection(con);
        return res;
    }
	
    private void unknownPacket(State state, int id) {
        log.warn(String.format("Unknown packet recived from Chat Server: 0x%02X state=%s", id, state.toString()));
    }
}