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

/**
 * @author Cura
 */
public class S_CAPTCHA extends AionServerPacket {

	private int type;
	private int count;
	private int size;
	private byte[] data;
	private boolean isCorrect;
	private int banTime;

	/**
	 * @param count
	 * @param data
	 */
	public S_CAPTCHA(int count, byte[] data) {
		this.type = 1;
		this.count = count;
		this.size = data.length;
		this.data = data;
	}

	/**
	 * @param isCorrect
	 */
	public S_CAPTCHA(boolean isCorrect, int banTime) {
		this.type = 3;
		this.isCorrect = isCorrect;
		this.banTime = banTime;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeC(type);

		switch (type) {
			case 0x01:
				writeC(count);
				writeD(size);
				writeB(data);
				break;
			case 0x03:
				writeH(isCorrect ? 1 : 0);

				// time setting can't be extracted (retail server default value:3000 sec)
				writeD(banTime);
				break;
		}
	}
}
