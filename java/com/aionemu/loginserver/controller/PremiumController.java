package com.aionemu.loginserver.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.loginserver.GameServerInfo;
import com.aionemu.loginserver.GameServerTable;
import com.aionemu.loginserver.dao.PremiumDAO;
import com.aionemu.loginserver.network.gameserver.serverpackets.SM_PREMIUM_RESPONSE;

/**
 * @author KID
 */
public class PremiumController {
	private Logger log = LoggerFactory.getLogger("PREMIUM_CTRL");
	private static PremiumController controller = new PremiumController();
	public static PremiumController getController() {
		return controller;
	}

	public static byte RESULT_FAIL = 1;
	public static byte RESULT_LOW_POINTS = 2;
	public static byte RESULT_OK = 3;
	public static byte RESULT_ADD = 4;

	public PremiumController() {
		log.info("PremiumController is ready for requests.");
	}

	public void requestBuy(int accountId, int requestId, long cost, byte serverId) {
		long toll = PremiumDAO.getToll(accountId);

		GameServerInfo server = GameServerTable.getGameServerInfo(serverId);
		if(server == null || server.getConnection() == null || !server.isAccountOnGameServer(accountId)) {
			log.error("Account "+accountId+" requested "+requestId+" from gs #"+serverId+" and server is down.");
			return;
		}

		//adding new tolls
		if(cost < 0) {
			long ncnt =  toll + (cost *-1);
			PremiumDAO.updateToll(accountId, ncnt, 0);
			server.getConnection().sendPacket(new SM_PREMIUM_RESPONSE(requestId, RESULT_ADD, ncnt));
			return;
		}

		if(toll < cost) {
			server.getConnection().sendPacket(new SM_PREMIUM_RESPONSE(requestId, RESULT_LOW_POINTS, toll));
			return;
		}

		if(PremiumDAO.updateToll(accountId, toll, cost)) {
			toll -= cost;
			server.getConnection().sendPacket(new SM_PREMIUM_RESPONSE(requestId, RESULT_OK, toll));
			log.info("Acount "+accountId+" succed in purchasing lot #"+requestId+" for "+cost+" from server #"+serverId);
		}
		else {
			server.getConnection().sendPacket(new SM_PREMIUM_RESPONSE(requestId, RESULT_FAIL, toll));
			log.info("Acount "+accountId+" failed in purchasing lot #"+requestId+" for "+cost+" from server #"+serverId+". !updatePoints");
		}
	}
}
