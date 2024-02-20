package com.aionemu.gameserver.model.templates.npc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NpcCommonDrop")
public class NpcCommonDrop
{
    @XmlAttribute(name = "item_id")
    protected int itemId;

    @XmlAttribute(name = "count")
    protected int count;

	@XmlAttribute(name = "prob")
    protected float prob;

    public int getItemId() {
        return itemId;
    }

    public int getCount() {
        return count;
    }

	public float getProb() {
        return prob;
    }
}