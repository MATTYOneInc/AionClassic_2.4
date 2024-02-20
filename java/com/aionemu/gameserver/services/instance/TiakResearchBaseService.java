package com.aionemu.gameserver.services.instance;

import com.aionemu.commons.services.CronService;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.configs.main.AutoGroupConfig;
import com.aionemu.gameserver.model.*;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_MATCHMAKER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.AutoGroupService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

import javolution.util.FastList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class TiakResearchBaseService
{
	private static final Logger log = LoggerFactory.getLogger(TiakResearchBaseService.class);
	
	private boolean registerAvailable;
	private final FastList<Integer> playersWithCooldown = FastList.newInstance();
	public static final byte minLevel = 50, capLevel = 56;
    public static final int maskId = 33;
	
	public void initTiakBase() {
		if (AutoGroupConfig.TIAK_BASE_ENABLED) {
			//Tiak Research Base MON-SUN "8PM-10PM"
			CronService.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					startTiakBaseRegistration();
				}
			}, AutoGroupConfig.TIAK_BASE_SCHEDULE_EVENING);
		}
	}
	
	private void startTiakBaseRegistration() {
		this.registerAvailable = true;
		startUregisterTiakBaseTask();
		Iterator<Player> iter = World.getInstance().getPlayersIterator();
        while (iter.hasNext()) {
            Player player = iter.next();
            if (player.getLevel() >= minLevel && player.getLevel() <= capLevel) {
                int instanceMaskId = getInstanceMaskId(player);
                if (instanceMaskId > 0) {
					///Players must first complete the quest ‘The Origin of Darkness’ to access Tiak Research Base.
					///https://www.aiononline.com/en-us/news/update-notes-3-22
					int questId = player.getRace() == Race.ASMODIANS ? 20005 : 10005;
					final QuestState qs = player.getQuestStateList().getQuestState(questId);
					if (qs == null || qs.getStatus() != QuestStatus.COMPLETE) {
						///You cannot use it as the required quest has not been completed.
						PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_CANNOT_MOVE_TO_AIRPORT_NEED_FINISH_QUEST);
					} else {
						PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(instanceMaskId, S_MATCHMAKER_INFO.wnd_EntryIcon));
						//An infiltration route into the Tiak Research Center is open.
						PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_OPEN_IDTiamatLab_War);
					}
                }
            }
        }
	}
	
	private void startUregisterTiakBaseTask() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				registerAvailable = false;
                playersWithCooldown.clear();
                AutoGroupService.getInstance().unRegisterInstance(maskId);
                Iterator<Player> iter = World.getInstance().getPlayersIterator();
                while (iter.hasNext()) {
                    Player player = iter.next();
                    if (player.getLevel() >= minLevel) {
                        int instanceMaskId = getInstanceMaskId(player);
                        if (instanceMaskId > 0) {
                            PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(instanceMaskId, S_MATCHMAKER_INFO.wnd_EntryIcon, true));
                        }
                    }
                }
			}
		}, AutoGroupConfig.TIAK_BASE_TIMER * 60 * 1000);
	}
	
	public boolean isTiakBaseAvailable() {
		return this.registerAvailable;
	}
	
	public int getInstanceMaskId(Player player) {
        int level = player.getLevel();
        if (level < minLevel || level >= capLevel) {
            return 0;
        }
        return maskId;
    }
	
    public void addCoolDown(Player player) {
        this.playersWithCooldown.add(player.getObjectId());
    }
	
    public boolean hasCoolDown(Player player) {
        return this.playersWithCooldown.contains(player.getObjectId());
    }
	
    public void showWindow(Player player, int instanceMaskId) {
        if (getInstanceMaskId(player) != instanceMaskId) {
            return;
        } if (!this.playersWithCooldown.contains(player.getObjectId())) {
            PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(instanceMaskId, 0));
        }
    }
	
	private static class SingletonHolder {
		protected static final TiakResearchBaseService instance = new TiakResearchBaseService();
	}
	
	public static TiakResearchBaseService getInstance() {
		return SingletonHolder.instance;
	}
}