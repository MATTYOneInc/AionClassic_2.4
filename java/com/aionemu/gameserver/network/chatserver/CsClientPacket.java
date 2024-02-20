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

import com.aionemu.commons.network.packet.BaseClientPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CsClientPacket extends BaseClientPacket<ChatServerConnection> implements Cloneable
{
    private static final Logger log = LoggerFactory.getLogger(CsClientPacket.class);
	
    protected CsClientPacket(int opcode) {
        super(opcode);
    }
	
    @Override
    public final void run() {
        try {
            runImpl();
        } catch (Throwable e) {
            log.warn("error handling ls (" + getConnection().getIP() + ") message " + this, e);
        }
    }
	
    protected void sendPacket(CsServerPacket msg) {
        getConnection().sendPacket(msg);
    }
	
    public CsClientPacket clonePacket() {
        try {
            return (CsClientPacket) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}