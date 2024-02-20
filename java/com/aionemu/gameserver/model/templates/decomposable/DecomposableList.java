package com.aionemu.gameserver.model.templates.decomposable;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlType(name = "DecomposableList")
public class DecomposableList {

    @XmlAttribute(name = "min_level")
    private int min_level;

    @XmlAttribute(name = "max_level")
    private int max_level;

    @XmlAttribute(name = "target_class")
    private PlayerClass playerClass = PlayerClass.ALL;

    @XmlAttribute(name = "race")
    private Race race = Race.PC_ALL;

    @XmlElement(name = "item")
    private List<DecomposableItemList> itemsCollections;

    public int getMin_level() {
        return min_level;
    }

    public int getMax_level() {
        return max_level;
    }

    public PlayerClass getPlayerClass() {
        return playerClass;
    }

    public Race getRace() {
        return race;
    }

    public List<DecomposableItemList> getItemsCollections() {
        return itemsCollections;
    }
}
