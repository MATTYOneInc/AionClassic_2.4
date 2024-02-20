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

import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.*;
import com.aionemu.gameserver.questEngine.model.*;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

import java.util.Iterator;

import java.util.concurrent.Future;

public class QuestUpdateService
{
	private Future<?> checkQuestUpdateTask;
	
    public void onStart() {
		//checkQuestUpdate();
    }
	
	private void checkQuestUpdate() {
		checkQuestUpdateTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				Iterator<Player> iter = World.getInstance().getPlayersIterator();
				while (iter.hasNext()) {
					final Player player = iter.next();
					if (player.getRace() == Race.ELYOS) {
					} else if (player.getRace() == Race.ASMODIANS) {
					}
				}
			}
		}, 5 * 1000, 5 * 1000);
	}
	
	public void onQuestUpdateLogin(final Player player) {
		if (player.getRace() == Race.ELYOS) {
		} else if (player.getRace() == Race.ASMODIANS) {
		}
    }
	
    public static QuestUpdateService getInstance() {
        return SingletonHolder.instance;
    }
	
	@SuppressWarnings("synthetic-access")
    private static class SingletonHolder {
        protected static final QuestUpdateService instance = new QuestUpdateService();
    }
}