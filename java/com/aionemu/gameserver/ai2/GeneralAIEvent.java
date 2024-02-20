package com.aionemu.gameserver.ai2;

import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.events.AbstractEvent;

public class GeneralAIEvent
        extends AbstractEvent<AbstractAI> {
    private static final long serialVersionUID = 5936695693551359627L;
    private final AIEventType eventType;

    public GeneralAIEvent(AbstractAI source, AIEventType eventType) {
        super(source);
        this.eventType = eventType;
    }

    public AIEventType getEventType() {
        return this.eventType;
    }
}
