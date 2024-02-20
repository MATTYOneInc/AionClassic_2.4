package com.aionemu.gameserver.model.templates.battle_pass;


import com.aionemu.gameserver.utils.gametime.DateTimeUtil;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "battlepass_season")
public class BattlePassSeasonTemplate {

    @XmlAttribute(name = "id", required = true)
    protected int id;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "max_level", required = true)
    protected int maxLevel;
    @XmlAttribute(name = "unlock_level_start", required = true)
    protected int unlockLevelStart;
    @XmlAttribute(name = "unlock_pass_cost", required = true)
    protected int unlockPassCost;
    @XmlAttribute(name = "unlock_level_cost", required = true)
    protected int unlockLevelCost;

    @XmlAttribute(name = "start", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar startDate;

    @XmlAttribute(name = "end", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar endDate;

    public int getId() {
        return id;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getUnlockLevelCost() {
        return unlockLevelCost;
    }

    public int getUnlockLevelStart() {
        return unlockLevelStart;
    }

    public int getUnlockPassCost() {
        return unlockPassCost;
    }

    public String getName() {
        return name;
    }

    public DateTime getStartDate() {
        return DateTimeUtil.getDateTime(startDate.toGregorianCalendar());
    }

    public DateTime getEndDate() {
        return DateTimeUtil.getDateTime(endDate.toGregorianCalendar());
    }
}
