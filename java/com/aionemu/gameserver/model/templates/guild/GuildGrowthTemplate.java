package com.aionemu.gameserver.model.templates.guild;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GuildGrowthTemplate")
public class GuildGrowthTemplate {

    @XmlAttribute(name = "level")
    private int level;

    @XmlAttribute(name = "guild_exp")
    private int exp;

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }
}
