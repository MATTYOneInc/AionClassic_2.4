package com.aionemu.gameserver.model.templates.battle_pass;

import com.aionemu.gameserver.model.Race;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "battlepass_quest")
public class BattlePassQuestTemplate {

    @XmlAttribute(name = "id", required = true)
    protected int id;

    @XmlAttribute(name = "type", required = true)
    protected BattleQuestType type;

    @XmlAttribute(name = "pass_id", required = true)
    protected int passId;

    @XmlAttribute(name = "action_id", required = true)
    protected int actionId;

    @XmlAttribute(name = "race", required = true)
    protected Race race;

    @XmlAttribute(name = "min_level", required = true)
    protected int minLevel;

    @XmlAttribute(name = "max_level", required = true)
    protected int maxLevel;

    @XmlAttribute(name = "complete_exp", required = true)
    protected int completeExp;

    @XmlAttribute(name = "start", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar startDate;

    @XmlAttribute(name = "end", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar endDate;

    public int getId() {
        return id;
    }

    public BattleQuestType getType() {
        return type;
    }

    public int getActionId() {
        return actionId;
    }

    public Race getRace() {
        return race;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getCompleteExp() {
        return completeExp;
    }

    public XMLGregorianCalendar getStartDate() {
        return startDate;
    }

    public XMLGregorianCalendar getEndDate() {
        return endDate;
    }

    public int getPassId() {
        return passId;
    }
}
