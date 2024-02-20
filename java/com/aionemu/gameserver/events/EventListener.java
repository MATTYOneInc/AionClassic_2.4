package com.aionemu.gameserver.events;

public interface EventListener<T extends AbstractEvent<?>> {
    public void onBeforeEvent(T var1);

    public void onAfterEvent(T var1);
}

