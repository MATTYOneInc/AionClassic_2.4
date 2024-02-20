package com.aionemu.gameserver.taskmanager.tasks;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.WebshopService;
import com.aionemu.gameserver.taskmanager.AbstractIterativePeriodicTaskManager;

public class PlayerBlackcloudTask extends AbstractIterativePeriodicTaskManager<Player> {


    protected PlayerBlackcloudTask() {
        super(15000);
    }

    @Override
    protected void callTask(Player player) {
        WebshopService.getInstance().check(player);
    }

    @Override
    protected String getCalledMethodName() {
        return "PlayerBlackcloud()";
    }
}
