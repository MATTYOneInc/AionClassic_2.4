package com.aionemu.gameserver.model.drop;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CommonItemTemplate")
public class CommonItemTemplate {

    @XmlAttribute(name = "item_id")
    protected int itemId;

    @XmlAttribute(name = "count")
    protected int count;

    public int getItemId() {
        return itemId;
    }

    public int getCount() {
        return count;
    }
}

