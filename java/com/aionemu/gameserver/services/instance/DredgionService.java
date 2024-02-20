package com.aionemu.gameserver.services.instance;

import com.aionemu.commons.services.CronService;
import com.aionemu.commons.network.util.ThreadPoolManager;

import com.aionemu.gameserver.configs.main.AutoGroupConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.S_MATCHMAKER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.services.AutoGroupService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

import javolution.util.FastList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class DredgionService
{
	private static final Logger log = LoggerFactory.getLogger(DredgionService.class);
	
	private boolean registerAvailable;
	private final FastList<Integer> playersWithCooldown = FastList.newInstance();
	public static final byte minLevel = 46, capLevel = 51;
    public static final int maskId = 1;
	
	public void initDredgion() {
		if (AutoGroupConfig.DREDGION_ENABLED) {
			//Dredgion MON-SUN "12PM-1PM"
			CronService.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					startDredgionRegistration1();
				}
			}, AutoGroupConfig.DREDGION_SCHEDULE_MIDDAY);
			//Dredgion MON-SUN "8PM-9PM"
			CronService.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					startDredgionRegistration2();
				}
			}, AutoGroupConfig.DREDGION_SCHEDULE_EVENING);
			//Dredgion MON-SUN "11PM-0AM"
			CronService.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					startDredgionRegistration3();
				}
			}, AutoGroupConfig.DREDGION_SCHEDULE_MIDNIGHT);
		}
	}
	
	private void startDredgionRegistration1() {
		this.registerAvailable = true;
		startUregisterDredgionTask();
		Iterator<Player> iter = World.getInstance().getPlayersIterator();
        while (iter.hasNext()) {
            Player player = iter.next();
            if (player.getLevel() >= minLevel && player.getLevel() <= capLevel) {
                int instanceMaskId = getInstanceMaskId(player);
                if (instanceMaskId > 0) {
                    PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(instanceMaskId, S_MATCHMAKER_INFO.wnd_EntryIcon));
					//An infiltration route into the Dredgion is open.
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_OPEN_IDAB1_DREADGION);
                }
            }
        }
	}
	private void startDredgionRegistration2() {
		this.registerAvailable = true;
		startUregisterDredgionTask();
		Iterator<Player> iter = World.getInstance().getPlayersIterator();
        while (iter.hasNext()) {
            Player player = iter.next();
            if (player.getLevel() >= minLevel && player.getLevel() <= capLevel) {
                int instanceMaskId = getInstanceMaskId(player);
                if (instanceMaskId > 0) {
                    PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(instanceMaskId, S_MATCHMAKER_INFO.wnd_EntryIcon));
					//An infiltration route into the Dredgion is open.
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_OPEN_IDAB1_DREADGION);
                }
            }
        }
	}
	private void startDredgionRegistration3() {
		this.registerAvailable = true;
		startUregisterDredgionTask();
		Iterator<Player> iter = World.getInstance().getPlayersIterator();
        while (iter.hasNext()) {
            Player player = iter.next();
            if (player.getLevel() >= minLevel && player.getLevel() <= capLevel) {
                int instanceMaskId = getInstanceMaskId(player);
                if (instanceMaskId > 0) {
                    PacketSendUtility.sendPacket(player, new S_MATCHMAKER_INFO(instanceMaskId, S_MATCHMAKER_INFO.wnd_EntryIcon));
					//An infiltration route into the Dredgion is open.
					PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_INSTANCE_OPEN_IDAB1_DREADGION);
                }
            }
        }
	}
	
	private void startUregisterDredgionTask() {
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
		}, AutoGroupConfig.DREDGION_TIMER * 60 * 1000);
	}
	
	public boolean isDredgionAvailable() {
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
		protected static final DredgionService instance = new DredgionService();
	}
	
	public static DredgionService getInstance() {
		return SingletonHolder.instance;
	}
}