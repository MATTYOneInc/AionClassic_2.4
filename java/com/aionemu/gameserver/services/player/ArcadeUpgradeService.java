package com.aionemu.gameserver.services.player;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.EventsConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerUpgradeArcade;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.templates.arcadeupgrade.ArcadeTab;
import com.aionemu.gameserver.model.templates.arcadeupgrade.ArcadeTabItem;
import com.aionemu.gameserver.network.aion.serverpackets.S_MESSAGE_CODE;
import com.aionemu.gameserver.network.aion.serverpackets.need.S_GOTCHA_NOTIFY;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import java.util.List;

public class ArcadeUpgradeService
{
    private final int frenzyTime = 90;

    public void onEnterWorld(Player player) {
        PacketSendUtility.sendPacket(player, new S_GOTCHA_NOTIFY(true));
    }

    public static ArcadeTabItem getRewardItem(Player player) {
        PlayerUpgradeArcade arcade = player.getUpgradeArcade();
        int frenzyLevel = arcade.getFrenzyLevel();
        boolean isFrenzy = arcade.isFrenzy();
        int rewardLevel = 1;
        if (frenzyLevel >= 3 && frenzyLevel <= 4) {
            rewardLevel = 2;
        } else if (frenzyLevel >= 5 && frenzyLevel <= 6) {
            rewardLevel = 3;
        } else if (frenzyLevel >= 7 && frenzyLevel <= 8) {
            rewardLevel = 4;
        }
        List<ArcadeTabItem> items = DataManager.ARCADE_UPGRADE_DATA.getArcadeTabById(rewardLevel);
        int count = (items.size() -1);
        return items.get(Rnd.get(0, count));
    }

    public static void getSpecialRewardItem(Player player) {
        PlayerUpgradeArcade arcade = player.getUpgradeArcade();
        List<ArcadeTabItem> items = DataManager.ARCADE_UPGRADE_DATA.getArcadeTabById(4);
        ArcadeTabItem item = items.get(Rnd.get(0, items.size()));
        int itemCount = arcade.isFrenzy() ? item.getNormalCount() : item.getFrenzyCount();
        if (itemCount == 0) {
            itemCount = item.getFrenzyCount();
        }
        PacketSendUtility.sendPacket(player, new S_GOTCHA_NOTIFY(11));
        //You've reached Frenzy four times and received %1 %0!
        PacketSendUtility.sendPacket(player, itemCount > 1 ? S_MESSAGE_CODE.STR_MSG_GACHA_FEVER_ITEM_REWARD_MULTI(item.getItemId(), itemCount) : S_MESSAGE_CODE.STR_MSG_GACHA_FEVER_ITEM_REWARD(item.getItemId()));
        ItemService.addItem(player, item.getItemId(), itemCount);
        arcade.setFrenzy(false);
    }

    public static ArcadeUpgradeService getInstance() {
        return SingletonHolder.instance;
    }

    public void closeWindow(Player player) {
        PacketSendUtility.sendPacket(player, new S_GOTCHA_NOTIFY(2));
    }

    public void startArcadeUpgrade(Player player) {
        PlayerUpgradeArcade arcade = player.getUpgradeArcade();
        if (arcade == null) {
            arcade = new PlayerUpgradeArcade();
        }
        arcade.reset();
        PacketSendUtility.sendPacket(player, new S_GOTCHA_NOTIFY(arcade.getFrenzyPoints(), arcade.getFrenzyCount()));
    }

    public void showRewardList(Player player) {
        PacketSendUtility.sendPacket(player, new S_GOTCHA_NOTIFY(10));
    }

    public List<ArcadeTab> getTabs() {
        return DataManager.ARCADE_UPGRADE_DATA.getArcadeTabs();
    }

    public void tryArcadeUpgrade(final Player player) {
        PlayerUpgradeArcade arcade = player.getUpgradeArcade();
        Storage localStorage = player.getInventory();
        if (player.getInventory().isFull()) {
            //Your cube is full.
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_FULL_INVENTORY);
            return;
        } if ((arcade.getFrenzyLevel() == 1) && (!localStorage.decreaseByItemId(186000389, 1))) {
            //You do not have enough %0s.
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_NOT_ENOUGH_TRADE_MONEY);
            return;
        } if (arcade.isReTry() && (!localStorage.decreaseByItemId(186000389, 2))) {
            //You do not have enough %0s.
            PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_NOT_ENOUGH_TRADE_MONEY);
            return;
        }
        if (arcade.isFailed() || arcade.getFrenzyLevel() == 1) {
            arcade.setFrenzyPoints(arcade.getFrenzyPoints() + 8);
            arcade.setFailed(false);
        } if (arcade.getFrenzyPoints() >= 100 && !arcade.isFrenzy()) {
            getFrenzyArcade(player, arcade);
        } if (Rnd.chance(EventsConfig.EVENT_ARCADE_CHANCE)) {
            getPlaySuccesArcade(player, arcade);
        } else {
            getPlayFailedArcade(player, arcade);
        }
    }

    public void getPlaySuccesArcade(final Player player, final PlayerUpgradeArcade arcade) {
        arcade.setFrenzyLevel(arcade.getFrenzyLevel() + 1);
        PacketSendUtility.sendPacket(player, new S_GOTCHA_NOTIFY(3, true, arcade.getFrenzyPoints()));
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                PacketSendUtility.sendPacket(player, new S_GOTCHA_NOTIFY(player, 4, arcade.getFrenzyLevel()));
            }
        }, 2000);
    }

    public void getPlayFailedArcade(final Player player, final PlayerUpgradeArcade arcade) {
        PacketSendUtility.sendPacket(player, new S_GOTCHA_NOTIFY(3, false, arcade.getFrenzyPoints()));
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (arcade.getFrenzyLevel() == 1 && !arcade.isReTry()) {
                    arcade.setFrenzyLevel(1);
                } else if (arcade.getFrenzyLevel() == 2 && !arcade.isReTry()) {
                    arcade.setFrenzyLevel(1);
                } else if (arcade.getFrenzyLevel() == 3 && !arcade.isReTry()) {
                    arcade.setFrenzyLevel(1);
                } else if (arcade.getFrenzyLevel() == 4 && !arcade.isReTry()) {
                    arcade.setFrenzyLevel(2);
                } else if (arcade.getFrenzyLevel() == 5 && !arcade.isReTry()) {
                    arcade.setFrenzyLevel(2);
                } else if (arcade.getFrenzyLevel() == 6 && !arcade.isReTry()) {
                    arcade.setFrenzyLevel(3);
                } else if (arcade.getFrenzyLevel() == 7 && !arcade.isReTry()) {
                    arcade.setFrenzyLevel(3);
                } else if (arcade.getFrenzyLevel() == 8 && !arcade.isReTry()) {
                    arcade.setFrenzyLevel(4);
                } else {
                    arcade.setReTry(true);
                    arcade.setFailedLevel(arcade.getFrenzyLevel());
                }
                PacketSendUtility.sendPacket(player, new S_GOTCHA_NOTIFY(player, 5, arcade.isReTry() ? arcade.getFailedLevel() : 1));
                arcade.setFailed(true);
            }
        }, 2000);
    }

    public void getFrenzyArcade(final Player player, final PlayerUpgradeArcade arcade) {
        if (arcade.getFrenzyCount() < 4) {
            arcade.setFrenzyCount(arcade.getFrenzyCount() + 1);
        } if (arcade.getFrenzyCount() == 4) {
            arcade.setFrenzy(true);
            getSpecialRewardItem(player);
        }
        //Upgrade Frenzy!
        PacketSendUtility.sendPacket(player, S_MESSAGE_CODE.STR_MSG_GACHA_FEVERTIME_START);
        PacketSendUtility.sendPacket(player, new S_GOTCHA_NOTIFY(7, frenzyTime, arcade.getFrenzyCount()));
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                PlayerUpgradeArcade arcade = player.getUpgradeArcade();
                PacketSendUtility.sendPacket(player, new S_GOTCHA_NOTIFY(7, 0, arcade.getFrenzyCount()));
                player.getUpgradeArcade().setFrenzy(false);
                if (arcade.getFrenzyCount() >= 4) {
                    arcade.setFrenzyCount(0);
                }
            }
        }, frenzyTime * 1000);
        player.getUpgradeArcade().setFrenzyPoints(0);
        if (arcade.getFrenzyCount() == 4) {
            arcade.setFrenzyCount(0);
        }
    }

    public void getReward(Player player) {
        PlayerUpgradeArcade arcade = player.getUpgradeArcade();
        ArcadeTabItem item = getRewardItem(player);
        int itemCount = arcade.isFrenzy() ? item.getNormalCount() : item.getFrenzyCount();
        if (arcade.isFrenzy()) {
            //You've reached Frenzy four times and received %1 %0!
            PacketSendUtility.sendPacket(player, itemCount >= 1 ? S_MESSAGE_CODE.STR_MSG_GACHA_FEVER_ITEM_REWARD_MULTI(item.getItemId(), itemCount) : S_MESSAGE_CODE.STR_MSG_GACHA_FEVER_ITEM_REWARD(item.getItemId()));
        } else {
            //You won %1 of %0 from the Upgrade Arcade.
            PacketSendUtility.sendPacket(player, itemCount >= 1 ? S_MESSAGE_CODE.STR_MSG_GACHA_ITEM_REWARD_MULTI(item.getItemId(), itemCount) : S_MESSAGE_CODE.STR_MSG_GACHA_ITEM_REWARD(item.getItemId()));
        } if (itemCount == 0) {
            ItemService.addItem(player, item.getItemId(), item.getFrenzyCount());
        } else {
            ItemService.addItem(player, item.getItemId(), itemCount);
        }
        PacketSendUtility.sendPacket(player, new S_GOTCHA_NOTIFY(6, item));
        arcade.reset();
    }

    private static class SingletonHolder {
        protected static final ArcadeUpgradeService instance = new ArcadeUpgradeService();
    }
}
