package com.aionemu.gameserver.services.player;

import com.aionemu.gameserver.configs.main.EventBoostConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;

public class BoostEventService {


    public void boostEventLogin(Player player) {
        if(EventBoostConfig.BOOST_EVENT_ENABLE) {
            player.getPlayerBoostEvent().apply(player);
        }
    }

    public void boostEventLogout(Player player) {
        if(EventBoostConfig.BOOST_EVENT_ENABLE) {
            player.getPlayerBoostEvent().end(player);
        }
    }

    private static class SingletonHolder {
        protected static final BoostEventService instance = new BoostEventService();
    }

    public static final BoostEventService getInstance() {
        return SingletonHolder.instance;
    }
}
