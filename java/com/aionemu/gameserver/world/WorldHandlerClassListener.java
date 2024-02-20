package com.aionemu.gameserver.world;

import com.aionemu.commons.scripting.classlistener.ClassListener;
import com.aionemu.commons.utils.ClassUtils;
import com.aionemu.gameserver.world.WorldEngine;
import com.aionemu.gameserver.world.WorldHandlerClassListener;
import com.aionemu.gameserver.world.handlers.WorldHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;

public class WorldHandlerClassListener implements ClassListener
{
    private static final Logger log = LoggerFactory.getLogger(WorldHandlerClassListener.class);

    @SuppressWarnings("unchecked")
    @Override
    public void postLoad(Class<?>[] classes) {
        for (Class<?> c : classes) {
            if (log.isDebugEnabled())
                log.debug("Load class " + c.getName());

            if (!isValidClass(c))
                continue;

            if (ClassUtils.isSubclass(c, WorldHandler.class)) {
                Class<? extends WorldHandler> tmp = (Class<? extends WorldHandler>) c;
                if (tmp != null) {
                    WorldEngine.getInstance().addWorldHandlerClass(tmp);
                }
            }
        }
    }

    @Override
    public void preUnload(Class<?>[] classes) {
        if (log.isDebugEnabled()) {
            for (Class<?> c : classes)
                log.debug("Unload class " + c.getName());
        }
    }

    public boolean isValidClass(Class<?> clazz) {
        final int modifiers = clazz.getModifiers();

        if (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers))
            return false;

        if (!Modifier.isPublic(modifiers))
            return false;

        return true;
    }
}
