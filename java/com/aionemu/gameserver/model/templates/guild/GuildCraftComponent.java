package com.aionemu.gameserver.model.templates.guild;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GuildCraftComponent")
public class GuildCraftComponent {

    @XmlAttribute(name = "item_id")
    private int itemId;
    @XmlAttribute(name = "quantity")
    private int quantity;

    public int getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }
}
