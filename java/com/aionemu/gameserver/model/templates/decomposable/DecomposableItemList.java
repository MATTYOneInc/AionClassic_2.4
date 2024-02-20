package com.aionemu.gameserver.model.templates.decomposable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "DecomposableItemList")
public class DecomposableItemList {

    @XmlAttribute(name = "id")
    private int id;

    @XmlAttribute(name = "count")
    private int count;

    public int getId() {
        return id;
    }

    public int getCount() {
        return count;
    }
}
