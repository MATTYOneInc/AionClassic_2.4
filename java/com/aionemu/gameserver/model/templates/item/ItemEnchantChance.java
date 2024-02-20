package com.aionemu.gameserver.model.templates.item;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlType(name = "ItemEnchantChance")
public class ItemEnchantChance {

    @XmlAttribute(name = "id")
    protected int enchantChanceId;
    @XmlElement(name = "item")
    private List<ItemEnchantChanceList> itemEnchantChanceList;

    public final int getId() {
        return enchantChanceId;
    }
	
    public ItemEnchantChanceList getChancesById(int id) {
        if (itemEnchantChanceList != null) {
            return itemEnchantChanceList.get(id);
        }
        return null;
    }

    public List<ItemEnchantChanceList> getItemEnchantChanceList() {
        return itemEnchantChanceList;
    }
}
