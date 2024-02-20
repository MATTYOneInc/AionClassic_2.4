package com.aionemu.gameserver.model.templates.event;

import com.aionemu.gameserver.utils.gametime.DateTimeUtil;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Timestamp;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventsWindowTemplate")
public class EventsWindowTemplate {

    @XmlAttribute(name = "id", required = true)
    private int id;

    @XmlAttribute(name = "item", required = true)
    private int item;

    @XmlAttribute(name = "count", required = true)
    private long count;

    @XmlAttribute(name = "period_start", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar pStart;

    @XmlAttribute(name = "period_end", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar pEnd;

    @XmlAttribute(name = "remaining_time", required = true)
    private int remaining_time;

    @XmlAttribute(name = "min_level", required = true)
    private int min_level;

    @XmlAttribute(name = "max_level", required = true)
    private int max_level;

    @XmlAttribute(name = "daily_max_count", required = true)
    private int dailyMaxCount;

    private Timestamp lastStamp;

    public int getId() {
        return id;
    }

    public int getItemId() {
        return item;
    }

    public long getCount() {
        return count;
    }

    public int getMaxCountOfDay() {
        return dailyMaxCount;
    }

    public DateTime getPeriodStart() {
        return DateTimeUtil.getDateTime(pStart.toGregorianCalendar());
    }

    public DateTime getPeriodEnd() {
        return DateTimeUtil.getDateTime(pEnd.toGregorianCalendar());
    }

    public int getRemainingTime() {
        return remaining_time;
    }

    public int getMinLevel() {
        return min_level;
    }

    public int getMaxLevel() {
        return max_level;
    }

    public Timestamp getLastStamp() {
        return lastStamp;
    }
}
