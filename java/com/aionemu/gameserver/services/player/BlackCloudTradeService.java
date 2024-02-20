package com.aionemu.gameserver.services.player;

import com.aionemu.gameserver.dao.AccountBlackCloudDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_NPSHOP_GOODS_COUNT;
import com.aionemu.gameserver.utils.PacketSendUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlackCloudTradeService {

    private Logger log = LoggerFactory.getLogger(BlackCloudTradeService.class);

    public void onLogin(Player player) {
        player.setBlackcloudLetters(AccountBlackCloudDAO.loadAccountBlackcloud(player.getPlayerAccount()));
        PacketSendUtility.sendPacket(player, new S_NPSHOP_GOODS_COUNT(player.getBlackcloudLetters().size()));
    }

    public void sendBlackCloudMail(Player player, int itemId, int itemCount) {

    }

    public void onOpenBlackCloudBox(Player player) {

    }

    public void onClaimItem(Player player, int objectId) {

    }

    public static BlackCloudTradeService getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        protected static final BlackCloudTradeService instance = new BlackCloudTradeService();
    }
}
