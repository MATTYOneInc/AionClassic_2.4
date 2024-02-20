package com.aionemu.gameserver.dataholders;

import com.aionemu.gameserver.model.templates.event.EventsWindowTemplate;
import gnu.trove.map.hash.TIntObjectHashMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "events_window")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class EventsWindowData {

    @XmlElement(name = "event_window")
    private List<EventsWindowTemplate> events_window;

    @XmlTransient
    private TIntObjectHashMap<EventsWindowTemplate> eventData = new TIntObjectHashMap<EventsWindowTemplate>();

    @XmlTransient
    private Map<Integer, EventsWindowTemplate> eventDataMap = new HashMap<>(1);

    void afterUnmarshal(Unmarshaller unmarshaller, Object object) {
        for (EventsWindowTemplate eventsWindow : events_window) {
            eventData.put(eventsWindow.getId(), eventsWindow);
            eventDataMap.put(eventsWindow.getId(), eventsWindow);
        }
    }

    public int size() {
        return eventData.size();
    }

    public EventsWindowTemplate getEventWindowId(int EventData) {
        return eventData.get(EventData);
    }

    public Map<Integer, EventsWindowTemplate> getAllEvents() {
        return eventDataMap;
    }
}
