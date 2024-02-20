/*
 *  Aion Classic Emu based on Aion Encom Source Files
 *
 *  ENCOM Team based on Aion-Lighting Open Source
 *  All Copyrights : "Data/Copyrights/AEmu-Copyrights.text
 *
 *  iMPERIVM.FUN - AION DEVELOPMENT FORUM
 *  Forum: <http://https://imperivm.fun/>
 *
 */
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class S_SEARCH_USER_RESULT extends AionServerPacket
{
	private static final Logger log = LoggerFactory.getLogger(S_SEARCH_USER_RESULT.class);

	private List<Player> players;
	private int region;
	
	public S_SEARCH_USER_RESULT(List<Player> players, int region) {
		this.players = new ArrayList<Player>(players);
		this.region = region;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		writeH(players.size());
		for (Player player : players) {
			if (player.getActiveRegion() == null) {
			   //log.warn("CHECKPOINT: null active region for " + player.getObjectId() + "-" + player.getX() + "-" + player.getY() + "-" + player.getZ());
			}
			writeD(player.getActiveRegion() == null ? region : player.getActiveRegion().getMapId());
			writeF(player.getPosition().getX());
			writeF(player.getPosition().getY());
			writeF(player.getPosition().getZ());
			writeC(player.getPlayerClass().getClassId());
			writeC(player.getGender().getGenderId());
			writeC(player.getLevel());
			if (player.isInGroup2())
				writeC(3);
			else if (player.isLookingForGroup())
				writeC(2);
			else
				writeC(0);
			writeS(player.getName(), 52);

		}
	}
}