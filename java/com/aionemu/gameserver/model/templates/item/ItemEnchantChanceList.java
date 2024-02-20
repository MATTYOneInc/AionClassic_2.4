package com.aionemu.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ItemEnchantChanceList")
public class ItemEnchantChanceList {

    @XmlAttribute(name = "level")
    private int level;
    @XmlAttribute(name = "chance")
    private int chance;
    @XmlAttribute(name = "crit")
    private int crit;

    public int getLevel() {
        return level;
    }

    public int getChance() {
        return chance;
    }

    public int getCrit() {
        return crit;
    }
}
