package com.aionemu.gameserver.services;

import com.aionemu.gameserver.dao.WebShopDAO;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.rewards.RewardEntryItem;
import com.aionemu.gameserver.services.player.BlackCloudTradeService;
import javolution.util.FastList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebshopService
{
    private static final Logger log = LoggerFactory.getLogger(WebshopService.class);

    public static final WebshopService getInstance() {
        return SingletonHolder.instance;
    }

    public void check(Player player) {
        FastList<RewardEntryItem> list = WebShopDAO.getAvailable(player.getPlayerAccount().getId());
        if (list.size() == 0) {
            return;
        }
        list.forEach(rewardEntryItem -> {
            BlackCloudTradeService.getInstance().sendBlackCloudMail(player, rewardEntryItem.id, rewardEntryItem.count);
            WebShopDAO.delete(rewardEntryItem.unique);
        });
    }

    @SuppressWarnings("synthetic-access")
    private static class SingletonHolder {
        protected static final WebshopService instance = new WebshopService();
    }
}
