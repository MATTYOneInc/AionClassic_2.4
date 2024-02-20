package com.aionemu.gameserver.model.templates.decomposable;

import com.aionemu.gameserver.model.templates.item.ExtractedItemsCollection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlType(name = "DecomposableTemplate")
public class DecomposableTemplate {

    @XmlAttribute(name = "id")
    private int id;

    @XmlElement(name = "items")
    private List<DecomposableList> items;

    public int getId() {
        return id;
    }

    public List<DecomposableList> getItems() {
        return items;
    }
}
