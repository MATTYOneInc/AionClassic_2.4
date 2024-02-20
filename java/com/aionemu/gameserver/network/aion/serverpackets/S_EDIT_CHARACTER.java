package com.aionemu.gameserver.network.aion.serverpackets;


import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * @author IlBuono
 */
public class S_EDIT_CHARACTER extends AionServerPacket {

	private int playerObjId;
	private byte check_ticket;
	private byte change_sex;

	public S_EDIT_CHARACTER(Player player, byte check_ticket, byte change_sex) {
		this.playerObjId = player.getObjectId();
		this.check_ticket = check_ticket;
		this.change_sex = change_sex;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(playerObjId);
		writeC(check_ticket);
		writeC(change_sex);
	}
}
