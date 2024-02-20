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


import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.utils.PacketSendUtility;

/**
 * @author xavier
 */
public class S_TODAY_WORDS extends AionServerPacket {

	private int targetObjId;
	private String note;

	public S_TODAY_WORDS(int targetObjId, String note) {
		this.targetObjId = targetObjId;
		this.note = note;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(targetObjId);
		writeS(note);
		PacketSendUtility.sendPacket(con.getActivePlayer(), new S_MESSAGE_CODE(1390124, note));
		System.out.print("work");
	}
}
