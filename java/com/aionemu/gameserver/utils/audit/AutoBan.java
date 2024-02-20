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
package com.aionemu.gameserver.utils.audit;

import com.aionemu.gameserver.configs.main.PunishmentConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.BannedMacManager;
import com.aionemu.gameserver.network.aion.serverpackets.S_ASK_QUIT_RESULT;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.services.PunishmentService;

/**
 * @author synchro2
 */
public class AutoBan {

	protected static void punishment(Player player, String message) {

		String reason = "AUTO " +message;
		String address = player.getClientConnection().getMacAddress();
		String accountIp = player.getClientConnection().getIP();
		int accountId = player.getClientConnection().getAccount().getId();
		int playerId = player.getObjectId();
		int time = PunishmentConfig.PUNISHMENT_TIME;
		int minInDay = 1440;
		int dayCount = (int)(Math.floor((double)(time/minInDay)));

		switch (PunishmentConfig.PUNISHMENT_TYPE) {
			case 1:
				player.getClientConnection().close(new S_ASK_QUIT_RESULT(), false);
				break;
			case 2:
				PunishmentService.banChar(playerId, dayCount, reason);
				break;
			case 3:
				LoginServer.getInstance().sendBanPacket((byte)1, accountId, accountIp, time, 0);
				break;
			case 4:
				LoginServer.getInstance().sendBanPacket((byte)2, accountId, accountIp, time, 0);
				break;
			case 5:
				player.getClientConnection().closeNow();
				BannedMacManager.getInstance().banAddress(address, System.currentTimeMillis() + time * 60000, reason);
				break;
		}
	}
}