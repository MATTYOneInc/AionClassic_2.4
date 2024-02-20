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
package com.aionemu.gameserver.services;

import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.ShutdownHook;

import com.aionemu.gameserver.services.player.BattlePassService;
import com.aionemu.gameserver.services.player.EventWindowService;
import com.aionemu.gameserver.services.player.SielEnergyService;
import org.slf4j.*;

import java.sql.Timestamp;
import java.util.Calendar;

public class RestartService
{
    private Logger log = LoggerFactory.getLogger(RestartService.class);
	
    public void onStart() {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
		String daily1 = "0 0 9 ? * * *";
        CronService.getInstance().schedule(new Runnable() {
            public void run() {
                BattlePassService.getInstance().onRestart();
                SielEnergyService.getInstance().onRestart();
                EventWindowService.getInstance().onRestart();
            }
        }, daily1);
        CronService.getInstance().schedule(new Runnable() {
            public void run() {
				int delay1 = 300;
                ShutdownHook.getInstance().doShutdown(delay1, 20, ShutdownHook.ShutdownMode.RESTART);
            }
        }, daily1);
    }
	
    public static RestartService getInstance() {
        return SingletonHolder.instance;
    }
	
    private static class SingletonHolder {
        protected static final RestartService instance = new RestartService();
    }
}