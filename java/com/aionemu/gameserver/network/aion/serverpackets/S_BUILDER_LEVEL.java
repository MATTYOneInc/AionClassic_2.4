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

public class S_BUILDER_LEVEL extends AionServerPacket
{
	public S_BUILDER_LEVEL() {
	}
	
	private boolean isGM;
    private int accountType;
    private int purchaseType;
    private int time;
	
	public S_BUILDER_LEVEL(boolean isGM) {
        this.isGM = isGM;
    }
	
    public S_BUILDER_LEVEL(boolean isGM, int accountType, int purchaseType, int time) {
		this.isGM = isGM;
        this.accountType = accountType;
        this.purchaseType = purchaseType;
        this.time = time;
    }
	
	@Override
    protected void writeImpl(AionConnection con) {
        writeB(new byte[29]);
    }
}