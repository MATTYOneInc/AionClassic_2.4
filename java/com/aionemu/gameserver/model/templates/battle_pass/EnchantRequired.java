package com.aionemu.gameserver.model.templates.battle_pass;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EnchantRequired")
public class EnchantRequired {

    @XmlAttribute(name = "level", required = true)
    protected int level;

    public int getLevel() {
        return level;
    }
}
