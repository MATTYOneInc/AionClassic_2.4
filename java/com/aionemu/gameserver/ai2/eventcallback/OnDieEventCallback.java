package com.aionemu.gameserver.ai2.eventcallback;

import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.ai2.GeneralAIEvent;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.events.EventListener;

/**
 * Callback for {@link AIEventType#DIED} event
 *
 * @author SoulKeeper
 */
public abstract class OnDieEventCallback implements EventListener<GeneralAIEvent> {

	@Override
	public void onBeforeEvent(GeneralAIEvent event) {
		if (AIEventType.DIED == event.getEventType()) {
			event.setHandled(true);
		}
	}

	@Override
	public void onAfterEvent(GeneralAIEvent event) {
		if (AIEventType.DIED == event.getEventType()) {
			event.setHandled(true);
		}
	}
}
