package com.aionemu.gameserver.model.templates.battle_pass;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PassReward")
public class PassReward {

    @XmlAttribute(name = "item_id")
    protected Integer itemId;

    @XmlAttribute(name = "count")
    protected Integer count;

    public Integer getItemId() {
        return itemId;
    }

    public Integer getCount() {
        return count;
    }
}
