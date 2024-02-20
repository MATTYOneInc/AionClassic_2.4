package com.aionemu.gameserver.model.templates.npc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NpcCash")
public class NpcCash {

    @XmlAttribute(name = "min_cash")
    protected int minCash;

    @XmlAttribute(name = "max_cash")
    protected int maxCash;

    public int getMinCash() {
        return minCash;
    }

    public int getMaxCash() {
        return maxCash;
    }
}
