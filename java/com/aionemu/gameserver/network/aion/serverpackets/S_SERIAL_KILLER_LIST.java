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

import java.util.Collection;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

public class S_SERIAL_KILLER_LIST extends AionServerPacket
{
	private int type;
	private int debuffLvl;
	private Collection<Player> players;
	
	public S_SERIAL_KILLER_LIST(boolean showMsg, int debuffLvl) {
		this.type = showMsg ? 1 : 0;
		this.debuffLvl = debuffLvl;
	}
	
	public S_SERIAL_KILLER_LIST(Collection<Player> players) {
		this.type = 4;
		this.players = players;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		switch (type) {
			case 0:
			case 1:
				writeD(type);
				writeD(0x01);
				writeD(0x01);
				writeH(0x01);
				writeD(debuffLvl);
			break;
			case 4:
				writeD(type);
				writeD(0x01);
				writeD(0x01);
				writeH(players.size());
				for (Player player : players) {
					writeD(player.getSKInfo().getRank());
					writeD(player.getObjectId());
					writeD(0x01);
					writeD(player.getAbyssRank().getRank().getId());
					writeH(player.getLevel());
					writeF(player.getX());
					writeF(player.getY());
					writeS(player.getName(), 118);
					writeD(0x00);
					writeD(0x00);
					writeD(0x00);
					writeD(0x00);
				}
			break;
		}
	}
}