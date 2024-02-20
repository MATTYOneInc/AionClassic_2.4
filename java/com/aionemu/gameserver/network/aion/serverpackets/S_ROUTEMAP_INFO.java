/*
 * This file is part of aion-unique <aionunique.smfnew.com>.
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
package com.aionemu.gameserver.network.aion.serverpackets;


import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.teleport.TeleporterTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S_ROUTEMAP_INFO extends AionServerPacket
{
	private int targetObjectId;
	private Player player;
	private TeleporterTemplate teleport;
	public Npc npc;
	
	private static final Logger log = LoggerFactory.getLogger(S_ROUTEMAP_INFO.class);
	
	public S_ROUTEMAP_INFO(Player player, int targetObjectId, TeleporterTemplate teleport) {
		this.player = player;
		this.targetObjectId = targetObjectId;
		this.npc = (Npc) World.getInstance().findVisibleObject(targetObjectId);
		this.teleport = teleport;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(targetObjectId);
		writeH(teleport.getTeleportId());
	}
}