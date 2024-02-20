package com.aionemu.gameserver.services.player;

import com.aionemu.gameserver.dao.PlayerEventsWindowDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.PlayerEventWindow;
import com.aionemu.gameserver.model.templates.event.EventsWindowTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.S_LOAD_PROMOTION;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.World;
import javolution.util.FastList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EventWindowService {
    private Logger log = LoggerFactory.getLogger(EventWindowService.class);

    private final List<EventsWindowTemplate> activeEvents = new FastList<EventsWindowTemplate>();

    public void onInit() {
        for (EventsWindowTemplate template : DataManager.EVENTS_WINDOW.getAllEvents().values()) {
            if (template.getPeriodStart().isBeforeNow() && template.getPeriodEnd().isAfterNow()) {
                activeEvents.add(template);
            }
        }
    }

    public void onEnterWorld(Player player) {
        player.setPlayerEventWindow(PlayerEventsWindowDAO.load(player));
		//log.info("player event windows size : " + player.getPlayerEventWindow().size());
        if (player.getPlayerEventWindow().size() == 0) {
            for (EventsWindowTemplate eventsWindow : activeEvents) {
                if (player.getLevel() >= eventsWindow.getMinLevel() && player.getLevel() <= eventsWindow.getMaxLevel()) {
                    PlayerEventWindow playerEventWindows = new PlayerEventWindow(eventsWindow.getId(), 0, 0);
                    PlayerEventsWindowDAO.insert(player.getPlayerAccount().getId(), playerEventWindows);
                    player.getPlayerEventWindow().put(playerEventWindows.getId(), playerEventWindows);
                }
            }
        } else {
            for (EventsWindowTemplate eventsWindow : activeEvents) {
                if (!player.getPlayerEventWindow().containsKey(eventsWindow.getId())) {
                    if (player.getLevel() >= eventsWindow.getMinLevel() && player.getLevel() <= eventsWindow.getMaxLevel()) {
                        PlayerEventWindow playerEventWindows = new PlayerEventWindow(eventsWindow.getId(), 0, 0);
                        PlayerEventsWindowDAO.insert(player.getPlayerAccount().getId(), playerEventWindows);
                        player.getPlayerEventWindow().put(playerEventWindows.getId(), playerEventWindows);
                    }
                }
            }

        }
        //log.info("player event windows size : " + player.getPlayerEventWindow().size());
        if (player.getPlayerEventWindow().size() != 0) {
            player.getController().addTask(TaskId.EVENT_WINDOW_UPDATE, ThreadPoolManager.getInstance().scheduleAtFixedRate(new EventWindowsUpdateTask(player.getObjectId()), 60000, 60000));

        }
        PacketSendUtility.sendPacket(player, new S_LOAD_PROMOTION(player));
    }

    public void onLogout(Player player) {
        int accountId = player.getPlayerAccount().getId();
        // WTF
    }

    public void onRestart() {
        PlayerEventsWindowDAO.delete();
    }

    public static EventWindowService getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        protected static final EventWindowService instance = new EventWindowService();
    }
}

class EventWindowsUpdateTask implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(SielEnergyUpdateTask.class);
    private final int playerId;

    EventWindowsUpdateTask(int playerId) {
        this.playerId = playerId;
    }

    public void run() {
        Player player = World.getInstance().findPlayer(playerId);
        if (player != null)
            try {
                player.getPlayerEventWindow().forEach((integer, eventWindow) -> {
                    if (eventWindow.getReceivedCount() == eventWindow.getTemplate().getMaxCountOfDay()) {

                    } else {
                        int newElapsed = eventWindow.getElapsed() + 1;
                        if (newElapsed == eventWindow.getTemplate().getRemainingTime()) {
                            eventWindow.setElapsed(0);
                            eventWindow.setReceivedCount(eventWindow.getReceivedCount() + 1);
                            PlayerEventsWindowDAO.update(player.getPlayerAccount().getId(), eventWindow);
                            ItemService.addItem(player, eventWindow.getTemplate().getItemId(), eventWindow.getTemplate().getCount());
                            //log.info("reward event id : " + eventWindow.getId());
                        } else {
                            //log.info("update +1");
                            eventWindow.setElapsed(newElapsed);
                            PlayerEventsWindowDAO.update(player.getPlayerAccount().getId(), eventWindow);
                        }
                    }
                });
                PacketSendUtility.sendPacket(player, new S_LOAD_PROMOTION(player));

                /*for (final PlayerEventWindow playerEventWindows : player.getPlayerEventWindow().values()) {
                    playerEventWindows.setElapsed(playerEventWindows.getElapsed() + 1);
                    final int elapsed = playerEventWindows.getElapsed();
                    final int recivedCount = playerEventWindows.getReceivedCount();
                    if (elapsed >= playerEventWindows.getTemplate().getRemainingTime()) {
                        playerEventWindows.setReceivedCount(playerEventWindows.getReceivedCount() + 1);
                        playerEventWindows.setElapsed(0);
                        playerEventWindows.setLastReceived(new Timestamp(System.currentTimeMillis()));

                        //ItemService.addItem(player, playerEventWindows.getTemplate().getItemId(), playerEventWindows.getTemplate().getCount());
                    } if (recivedCount == playerEventWindows.getTemplate().getMaxCountOfDay()) {
                        player.getPlayerEventWindow().remove(playerEventWindows.getId());
                        return;
                    }
                    PacketSendUtility.sendPacket(player, new SM_EVENT_WINDOW(player));
                }*/
            } catch (Exception ex) {
            }
    }
}
