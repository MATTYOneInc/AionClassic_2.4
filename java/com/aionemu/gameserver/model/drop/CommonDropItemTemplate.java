package com.aionemu.gameserver.model.drop;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CommonDropItemTemplate")
public class CommonDropItemTemplate {

    @XmlAttribute(name = "id")
    protected int id;

    @XmlElement(name = "common_item")
    private List<CommonItemTemplate> commonItems;

    public int getId() {
        return id;
    }

    public List<CommonItemTemplate> getCommonItems() {
        return commonItems;
    }
}
