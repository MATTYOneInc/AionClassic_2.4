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
import com.aionemu.gameserver.services.trade.PricesService;

public class S_TAX_INFO extends AionServerPacket
{
	@Override
	protected void writeImpl(AionConnection con) {
		writeC(PricesService.getGlobalPrices(con.getActivePlayer().getRace()));
		writeC(PricesService.getGlobalPricesModifier());
		writeC(PricesService.getTaxes(con.getActivePlayer().getRace()));
	}
}