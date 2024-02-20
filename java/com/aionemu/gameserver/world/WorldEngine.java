package com.aionemu.gameserver.world;

import com.aionemu.commons.scripting.classlistener.AggregatedClassListener;
import com.aionemu.commons.scripting.classlistener.OnClassLoadUnloadListener;
import com.aionemu.commons.scripting.classlistener.ScheduledTaskClassListener;
import com.aionemu.commons.scripting.scriptmanager.ScriptManager;
import com.aionemu.gameserver.GameServerError;
import com.aionemu.gameserver.world.handlers.GeneralWorldHandler;
import com.aionemu.gameserver.world.handlers.WorldHandler;
import com.aionemu.gameserver.world.handlers.WorldID;
import com.aionemu.gameserver.model.GameEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class WorldEngine implements GameEngine
{
    private static final Logger log = LoggerFactory.getLogger(WorldEngine.class);
    private static ScriptManager scriptManager = new ScriptManager();
    public static final File INSTANCE_DESCRIPTOR_FILE = new File("./data/scripts/system/worldhandlers.xml");
    public static final WorldHandler DUMMY_INSTANCE_HANDLER = new GeneralWorldHandler();

    private Map<Integer, Class<? extends WorldHandler>> handlers = new HashMap<Integer, Class<? extends WorldHandler>>();

    @Override
    public void load(CountDownLatch progressLatch) {
        log.info("Map engine load started");
        scriptManager = new ScriptManager();
        AggregatedClassListener acl = new AggregatedClassListener();
        acl.addClassListener(new OnClassLoadUnloadListener());
        acl.addClassListener(new ScheduledTaskClassListener());
        acl.addClassListener(new WorldHandlerClassListener());
        scriptManager.setGlobalClassListener(acl);
        try {
            scriptManager.load(INSTANCE_DESCRIPTOR_FILE);
            log.info("Loaded " + handlers.size() + " World Script");
        } catch (Exception e) {
            throw new GameServerError("Can't initialize map handlers.", e);
        } finally {
            if (progressLatch != null) {
                progressLatch.countDown();
            }
        }
    }

    @Override
    public void shutdown() {
        log.info("Map engine shutdown started");
        scriptManager.shutdown();
        scriptManager = null;
        handlers.clear();
        log.info("Map engine shutdown complete");
    }

    public WorldHandler getNewInstanceHandler(int worldId) {
        Class<? extends WorldHandler> instanceClass = handlers.get(worldId);
        WorldHandler worldHandler = null;
        if (instanceClass != null) {
            try {
                worldHandler = instanceClass.newInstance();
            }
            catch (Exception ex) {
                log.warn("Can't instantiate map handler " + worldId, ex);
            }
        } if (worldHandler == null) {
            worldHandler = DUMMY_INSTANCE_HANDLER;
        }
        return worldHandler;
    }

    final void addWorldHandlerClass(Class<? extends WorldHandler> handler) {
        WorldID idAnnotation = handler.getAnnotation(WorldID.class);
        if (idAnnotation != null) {
            handlers.put(idAnnotation.value(), handler);
        }
    }

    public void onWorldCreate(final WorldMap map) {
        map.getWorldHandler().onWorldCreate(map);
    }

    public static final WorldEngine getInstance() {
        return WorldEngine.SingletonHolder.instance;
    }

    @SuppressWarnings("synthetic-access")
    private static class SingletonHolder {
        protected static final WorldEngine instance = new WorldEngine();
    }
}
